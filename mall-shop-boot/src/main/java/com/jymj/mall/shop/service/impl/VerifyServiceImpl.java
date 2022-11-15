package com.jymj.mall.shop.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.order.api.OrderFeignClient;
import com.jymj.mall.order.vo.MallOrderInfo;
import com.jymj.mall.order.vo.MallOrderMdseDetailsInfo;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import com.jymj.mall.shop.dto.VerifyPersonDTO;
import com.jymj.mall.shop.dto.VerifyPersonPageQuery;
import com.jymj.mall.shop.entity.VerifyOrder;
import com.jymj.mall.shop.entity.VerifyPerson;
import com.jymj.mall.shop.repository.VerifyOrderRepository;
import com.jymj.mall.shop.repository.VerifyPersonRepository;
import com.jymj.mall.shop.service.VerifyService;
import com.jymj.mall.shop.vo.VerifyPersonInfo;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.vo.UserInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 核销
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {

    private final VerifyPersonRepository verifyPersonRepository;
    private final ThreadPoolTaskExecutor executor;
    private final VerifyOrderRepository verifyOrderRepository;
    private final UserFeignClient userFeignClient;
    private final AdminFeignClient adminFeignClient;
    private final OrderFeignClient orderFeignClient;


    @Override
    @GlobalTransactional(name = "mall-shop-verify-add", rollbackFor = Exception.class)
    public VerifyPerson add(VerifyPersonDTO dto) {

        if ((dto.getUserId() == null && dto.getAdminId() == null) || CollectionUtils.isEmpty(dto.getMdseIdList())) {
            throw new BusinessException("参数错误");
        }
        Optional<VerifyPerson> verifyPersonOptional = findByUserIdOrAdminId(dto.getUserId(), dto.getAdminId());

        if (verifyPersonOptional.isPresent()) {
            throw new BusinessException("核销人员已存在");
        }
        VerifyPerson verifyPerson = new VerifyPerson();

        Optional<UserInfo> userInfoOptional = setUserAsVerifyPerson(dto.getUserId(), SystemConstants.STATUS_OPEN);
        userInfoOptional.ifPresent(userInfo -> {
            verifyPerson.setUserName(userInfo.getNickName());
            verifyPerson.setUserId(dto.getUserId());
        });

        Optional<AdminInfo> adminInfoOptional = setAdminAsVerifyPerson(dto.getAdminId(), SystemConstants.STATUS_OPEN);
        adminInfoOptional.ifPresent(adminInfo -> {
            verifyPerson.setAdminName(adminInfo.getNickname());
            verifyPerson.setAdminId(dto.getAdminId());
        });

        verifyPerson.setShopIds(dto.getShopIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        verifyPerson.setMdseIds(dto.getMdseIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        verifyPerson.setDeleted(SystemConstants.DELETED_NO);

        return verifyPersonRepository.save(verifyPerson);
    }

    private Optional<AdminInfo> setAdminAsVerifyPerson(Long adminId, Integer verifyPersonStatus) {
        if (adminId != null && verifyPersonStatus != null) {
            Result<AdminInfo> adminInfoResult = adminFeignClient.getAdminById(adminId);
            if (!Result.isSuccess(adminInfoResult)) {
                throw new BusinessException("用户不存在");
            }
            if (!verifyPersonStatus.equals(adminInfoResult.getData().getVerifyPerson())) {
                adminInfoResult = adminFeignClient.updateAdmin(UpdateAdminDTO.builder().adminId(adminId).verifyPerson(verifyPersonStatus).build());
                if (!Result.isSuccess(adminInfoResult)) {
                    throw new BusinessException("核销员用户设置失败");
                }
            }
            return Optional.of(adminInfoResult.getData());
        }
        return Optional.empty();
    }

    private Optional<UserInfo> setUserAsVerifyPerson(Long userId, Integer verifyPersonStatus) {
        if (userId != null && verifyPersonStatus != null) {
            Result<UserInfo> userInfoResult = userFeignClient.getUserById(userId);
            if (!Result.isSuccess(userInfoResult)) {
                throw new BusinessException("用户不存在");
            }

            userInfoResult = userFeignClient.updateUser(UserDTO.builder().userId(userId).verifyPerson(verifyPersonStatus).build());
            if (!Result.isSuccess(userInfoResult)) {
                throw new BusinessException("核销员用户设置失败");
            }

            return Optional.of(userInfoResult.getData());
        }
        return Optional.empty();
    }

    private Optional<VerifyPerson> findByUserIdOrAdminId(Long userId, Long adminId) {
        Optional<VerifyPerson> verifyPersonOptional = Optional.empty();

        if (userId != null) {
            verifyPersonOptional = verifyPersonRepository.findByUserId(userId);
        }
        if (adminId != null && !verifyPersonOptional.isPresent()) {
            verifyPersonOptional = verifyPersonRepository.findByAdminId(adminId);
        }

        return verifyPersonOptional;
    }


    @Override
    @GlobalTransactional(name = "mall-shop-verify-update", rollbackFor = Exception.class)
    public Optional<VerifyPerson> update(VerifyPersonDTO dto) {
        if ((dto.getUserId() == null && dto.getAdminId() == null) || CollectionUtils.isEmpty(dto.getMdseIdList())) {
            throw new BusinessException("参数错误");
        }
        Optional<VerifyPerson> verifyPersonOptional = findByUserIdOrAdminId(dto.getUserId(), dto.getAdminId());

        VerifyPerson verifyPerson = verifyPersonOptional.orElseThrow(() -> new BusinessException("核销人员不存在"));
        if (Objects.nonNull(dto.getUserId())) {
            if (Objects.nonNull(verifyPerson.getUserId()) && !dto.getUserId().equals(verifyPerson.getUserId())) {
                throw new BusinessException("该用户已绑定其他人员");
            }
            Optional<UserInfo> userInfoOptional = setUserAsVerifyPerson(dto.getUserId(), SystemConstants.STATUS_OPEN);
            userInfoOptional.ifPresent(userInfo -> {
                verifyPerson.setUserName(userInfo.getNickName());
                verifyPerson.setUserId(userInfo.getUserId());
            });
        }
        if (Objects.nonNull(dto.getAdminId())) {
            if (Objects.nonNull(verifyPerson.getAdminId()) && !dto.getAdminId().equals(verifyPerson.getAdminId())) {
                throw new BusinessException("该管理员已绑定其他人员");
            }
            Optional<AdminInfo> adminInfoOptional = setAdminAsVerifyPerson(dto.getAdminId(), SystemConstants.STATUS_OPEN);
            adminInfoOptional.ifPresent(adminInfo -> {
                verifyPerson.setAdminName(adminInfo.getNickname());
                verifyPerson.setAdminId(adminInfo.getAdminId());
            });
        }

        verifyPerson.setShopIds(dto.getShopIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        verifyPerson.setMdseIds(dto.getMdseIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));

        return Optional.of(verifyPersonRepository.save(verifyPerson));
    }

    @Override
    @GlobalTransactional(name = "mall-shop-verify-delete", rollbackFor = Exception.class)
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<VerifyPerson> verifyPersonList = verifyPersonRepository.findAllById(idList);
        List<Long> userIdList = verifyPersonList.stream().map(VerifyPerson::getUserId).filter(userId -> !ObjectUtils.isEmpty(userId)).collect(Collectors.toList());
        userIdList.forEach(userId -> setUserAsVerifyPerson(userId, SystemConstants.STATUS_CLOSE));
        List<Long> adminIdList = verifyPersonList.stream().map(VerifyPerson::getAdminId).filter(adminId -> !ObjectUtils.isEmpty(adminId)).collect(Collectors.toList());
        adminIdList.forEach(adminId -> setAdminAsVerifyPerson(adminId, SystemConstants.STATUS_CLOSE));
        verifyPersonRepository.deleteAllById(idList);
    }

    @Override
    public Optional<VerifyPerson> findById(Long id) {
        return verifyPersonRepository.findById(id);
    }

    @Override
    public VerifyPersonInfo entity2vo(VerifyPerson entity) {
        if (Objects.nonNull(entity)) {
            VerifyPersonInfo verifyPersonInfo = new VerifyPersonInfo();
            verifyPersonInfo.setId(entity.getId());

            if (entity.getUserId() != null) {
                Result<UserInfo> userInfoResult = userFeignClient.getUserById(entity.getUserId());
                if (Result.isSuccess(userInfoResult)) {
                    verifyPersonInfo.setUserId(entity.getUserId());
                    verifyPersonInfo.setUserInfo(userInfoResult.getData());
                }
            }
            if (entity.getAdminId() != null) {
                Result<AdminInfo> adminInfoResult = adminFeignClient.getAdminById(entity.getAdminId());
                if (Result.isSuccess(adminInfoResult)) {
                    verifyPersonInfo.setAdminId(entity.getAdminId());
                    verifyPersonInfo.setAdminInfo(adminInfoResult.getData());
                }
            }

            if (StringUtils.hasText(entity.getMdseIds())) {
                verifyPersonInfo.setMdseIds(Arrays.stream(entity.getMdseIds().split(",")).map(Long::parseLong).collect(Collectors.toList()));
            }
            if (StringUtils.hasText(entity.getShopIds())) {
                verifyPersonInfo.setShopIdList(Arrays.stream(entity.getShopIds().split(",")).map(Long::parseLong).collect(Collectors.toList()));
            }


            return verifyPersonInfo;
        }
        return null;
    }

    @Override
    public List<VerifyPersonInfo> list2vo(List<VerifyPerson> entityList) {
        List<CompletableFuture<VerifyPersonInfo>> futureList = Optional.of(entityList).orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VerifyPerson> findPage(VerifyPersonPageQuery verifyPersonPageQuery) {
        Pageable pageable = PageUtils.getPageable(verifyPersonPageQuery);
        Specification<VerifyPerson> specification = (root, query, builder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (StringUtils.hasText(verifyPersonPageQuery.getUserName())) {
                list.add(builder.like(root.get("userName").as(String.class), SystemConstants.generateSqlLike(verifyPersonPageQuery.getUserName())));
            }
            if (StringUtils.hasText(verifyPersonPageQuery.getAdminName())) {
                list.add(builder.like(root.get("adminName").as(String.class), SystemConstants.generateSqlLike(verifyPersonPageQuery.getAdminName())));
            }
            Predicate[] p = new Predicate[list.size()];
            return builder.and(list.toArray(p));
        };
        return verifyPersonRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<VerifyPerson> findByAdminId(Long adminId) {
        return verifyPersonRepository.findByAdminId(adminId);
    }

    @Override
    public Optional<VerifyPerson> findByUserId(Long userId) {
        return verifyPersonRepository.findByUserId(userId);
    }

    @Override
    public List<VerifyPerson> findAllByAdminIdIn(List<Long> adminIds) {

        return verifyPersonRepository.findAllByAdminIdIn(adminIds);
    }

    @Override
    public void deleteAll(List<VerifyPerson> verifyPeopleList) {
        List<Long> userIdList = verifyPeopleList.stream().map(VerifyPerson::getUserId).filter(userId -> !ObjectUtils.isEmpty(userId)).collect(Collectors.toList());
        userIdList.forEach(userId -> setUserAsVerifyPerson(userId, SystemConstants.STATUS_CLOSE));
        List<Long> adminIdList = verifyPeopleList.stream().map(VerifyPerson::getAdminId).filter(adminId -> !ObjectUtils.isEmpty(adminId)).collect(Collectors.toList());
        adminIdList.forEach(adminId -> setAdminAsVerifyPerson(adminId, SystemConstants.STATUS_CLOSE));
        verifyPersonRepository.deleteAll(verifyPeopleList);
    }

    @Override
    @GlobalTransactional(name = "mall-shop-order-verify", rollbackFor = Exception.class)
    public void verify(VerifyOrderMdse verifyOrderMdse) {
        Long userId = UserUtils.getUserId();
        Optional<VerifyPerson> verifyPersonOptional = findByUserId(userId);
        if (!verifyPersonOptional.isPresent()) {
            throw new BusinessException("权限不足");
        }
        VerifyPerson verifyPerson = verifyPersonOptional.get();
        if (!verifyPerson.getMdseIds().contains(String.valueOf(verifyOrderMdse.getMdseId()))) {
            throw new BusinessException("没有该商品核销权限");
        }
        Result<MallOrderInfo> orderInfoResult = orderFeignClient.getOrderById(verifyOrderMdse.getOrderId());
        if (!Result.isSuccess(orderInfoResult)) {
            throw new BusinessException("订单不存在");
        }
        MallOrderInfo orderInfo = orderInfoResult.getData();
        List<MallOrderMdseDetailsInfo> orderMdseDetailsInfoList = orderInfo.getOrderMdseDetailsInfoList();
        Optional<MallOrderMdseDetailsInfo> orderMdseDetailsInfo = orderMdseDetailsInfoList.stream()
                .filter(orderMdse -> verifyOrderMdse.getMdseId().equals(orderMdse.getMdseId())
                        && verifyOrderMdse.getStockId().equals(orderMdse.getStockId())
                        && SystemConstants.STATUS_CLOSE.equals(orderMdse.getUsageStatus()))
                .findFirst();

        if (!orderMdseDetailsInfo.isPresent()) {
            throw new BusinessException("商品不存在");
        }
        Result<Object> verify = orderFeignClient.verify(verifyOrderMdse);
        if (!Result.isSuccess(verify)) {
            throw new BusinessException("核销失败");
        }
        MallOrderMdseDetailsInfo mdseDetailsInfo = orderMdseDetailsInfo.get();
        VerifyOrder verifyOrder = new VerifyOrder();
        verifyOrder.setUserId(userId);
        verifyOrder.setAdminId(verifyPerson.getAdminId());
        verifyOrder.setOrderId(orderInfo.getOrderId());
        verifyOrder.setShopId(verifyOrder.getShopId());
        verifyOrder.setMdseId(mdseDetailsInfo.getMdseId());
        verifyOrder.setStockId(mdseDetailsInfo.getStockId());
        verifyOrder.setDeleted(SystemConstants.DELETED_NO);
        verifyOrderRepository.save(verifyOrder);
    }
}
