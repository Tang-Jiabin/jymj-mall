package com.jymj.mall.user.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jymj.mall.admin.api.RoleFeignClient;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.marketing.api.CouponFeignClient;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.dto.MdsePurchaseRecordDTO;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.dto.UserPageQuery;
import com.jymj.mall.user.entity.MallUser;
import com.jymj.mall.user.entity.MallUserRole;
import com.jymj.mall.user.enums.MemberEnum;
import com.jymj.mall.user.enums.SourceEnum;
import com.jymj.mall.user.repository.MallUserRoleRepository;
import com.jymj.mall.user.repository.UserRepository;
import com.jymj.mall.user.service.UserService;
import com.jymj.mall.user.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MallUserRoleRepository userRoleRepository;
    private final RoleFeignClient roleFeignClient;
    private final ThreadPoolTaskExecutor executor;
    private final MdseFeignClient mdseFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final CouponFeignClient couponFeignClient;


    @Override
    public MallUser add(UserDTO dto) {

        if (StringUtils.hasText(dto.getOpenid())) {
            Optional<MallUser> mallUserOptional = userRepository.findByOpenid(dto.getOpenid());
            if (mallUserOptional.isPresent()) {
                return mallUserOptional.get();
            }
        }
        MallUser user = new MallUser();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickName());
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setGender(dto.getGender());
        user.setMobile(dto.getMobile());
        user.setBirthday(dto.getBirthday());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setOpenid(dto.getOpenid());
        user.setCity(dto.getCity());
        user.setCountry(dto.getCountry());
        user.setLanguage(dto.getLanguage());
        user.setProvince(dto.getProvince());
        user.setStatus(SystemConstants.STATUS_OPEN);
        user.setDeleted(SystemConstants.DELETED_NO);
        user.setMemberType(MemberEnum.ORDINARY_USER);
        user.setLoginTime(new Date());
        user.setSourceType(dto.getSourceType());
        user.setPurchaseCount(0);
        user.setMemberLevel(0);
        user = userRepository.save(user);

        MallUserRole userRole = new MallUserRole();
        userRole.setRoleId(14L);
        userRole.setUserId(user.getUserId());
        userRole.setDeleted(SystemConstants.DELETED_NO);
        userRoleRepository.save(userRole);

        //TODO 发放新人优惠券 此处应该使用MQ异步处理 为了方便演示直接使用线程池
        MallUser finalUser = user;
//        executor.execute(() -> {
//            UserCouponDTO userCouponDTO = new UserCouponDTO();
//            userCouponDTO.setUserId(finalUser.getUserId());
//            userCouponDTO.setCouponTemplateId(1L);
//            userCouponDTO.setStatus(CouponStateEnum.NORMAL);
//            Result<Object> result = couponFeignClient.addUserCoupon(userCouponDTO);
//            log.info("发放新人优惠券结果:{}", result);
//        });

        return user;
    }


    @Override
    @CacheEvict(value = "mall-user:user-info:", allEntries = true)
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallUser> brandList = userRepository.findAllById(idList);
            userRepository.deleteAll(brandList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "mall-user:user-info:", key = "'user-id:'+#dto.userId")
    public Optional<MallUser> update(UserDTO dto) {
        if (!ObjectUtils.isEmpty(dto) && !ObjectUtils.isEmpty(dto.getUserId())) {
            Optional<MallUser> userOptional = userRepository.findById(dto.getUserId());
            if (userOptional.isPresent()) {
                MallUser user = userOptional.get();
                boolean update = baseUpdate(dto, user);
                if (update) {
                    return Optional.of(userRepository.save(user));
                }
            }
        }

        return Optional.empty();
    }

    private static boolean baseUpdate(UserDTO dto, MallUser user) {
        boolean update = false;
        if (StringUtils.hasText(dto.getNickName())) {
            user.setNickname(dto.getNickName());
            update = true;
        }
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(dto.getPassword());
            update = true;
        }
        if (StringUtils.hasText(dto.getMobile())) {
            user.setMobile(dto.getMobile());
            update = true;
        }
        if (StringUtils.hasText(dto.getAvatarUrl())) {
            user.setAvatarUrl(dto.getAvatarUrl());
            update = true;
        }
        if (StringUtils.hasText(dto.getCity())) {
            user.setCity(dto.getCity());
            update = true;
        }
        if (StringUtils.hasText(dto.getCountry())) {
            user.setCountry(dto.getCountry());
            update = true;
        }
        if (StringUtils.hasText(dto.getLanguage())) {
            user.setLanguage(dto.getLanguage());
            update = true;
        }
        if (StringUtils.hasText(dto.getProvince())) {
            user.setProvince(dto.getProvince());
            update = true;
        }
        if (!ObjectUtils.isEmpty(dto.getGender())) {
            user.setGender(dto.getGender());
            update = true;
        }
        if (!ObjectUtils.isEmpty(dto.getBirthday())) {
            user.setBirthday(dto.getBirthday());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getMemberType())) {
            user.setMemberType(dto.getMemberType());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getSourceType())) {
            user.setSourceType(dto.getSourceType());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getLoginTime())) {
            user.setLoginTime(dto.getLoginTime());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getPurchaseCount())) {
            user.setPurchaseCount(dto.getPurchaseCount());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getVerifyPerson())) {
            user.setVerifyPerson(dto.getVerifyPerson());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getMemberLevel())) {
            user.setMemberLevel(dto.getMemberLevel());
            update = true;
        }
        if (StringUtils.hasText(dto.getMemberName())) {
            user.setMemberName(dto.getMemberName());
            update = true;
        }
        if (StringUtils.hasText(dto.getMemberMobile())) {
            user.setMemberMobile(dto.getMemberMobile());
            update = true;
        }

        return update;
    }

    @Override
    public Optional<MallUser> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserInfo entity2vo(MallUser entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(entity.getUserId());
            userInfo.setUsername(entity.getUsername());
            userInfo.setNickName(entity.getNickname());
            userInfo.setMobile(entity.getMobile());
            userInfo.setGender(entity.getGender());
            userInfo.setBirthday(entity.getBirthday());
            userInfo.setAvatarUrl(entity.getAvatarUrl());
            userInfo.setCity(entity.getCity());
            userInfo.setCountry(entity.getCountry());
            userInfo.setLanguage(entity.getLanguage());
            userInfo.setProvince(entity.getProvince());
            userInfo.setStatus(entity.getStatus());
            userInfo.setCreateTime(entity.getCreateTime());
            userInfo.setLoginTime(entity.getLoginTime());
            userInfo.setSourceType(entity.getSourceType());
            userInfo.setMemberType(entity.getMemberType());
            userInfo.setPurchaseCount(entity.getPurchaseCount());
            userInfo.setVerifyPerson(entity.getVerifyPerson());
            userInfo.setMemberLevel(entity.getMemberLevel());
            userInfo.setMemberName(entity.getMemberName());
            userInfo.setMemberMobile(entity.getMemberMobile());
            return userInfo;
        }
        return null;
    }

    @Override
    public List<UserInfo> list2vo(List<MallUser> entityList) {
        List<CompletableFuture<UserInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MallUser> findPage(UserPageQuery userPageQuery) {
        Pageable pageable = PageUtils.getPageable(userPageQuery);


        Specification<MallUser> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            CriteriaBuilder.In<Long> userIdIn = criteriaBuilder.in(root.get("userId").as(Long.class));
            Set<Long> userIdSet = Sets.newHashSet();

            String mobile = userPageQuery.getMobile();
            if (StringUtils.hasText(mobile)) {
                list.add(criteriaBuilder.like(root.get("mobile"), SystemConstants.generateSqlLike(mobile)));
            }
            String nickname = userPageQuery.getNickname();
            if (StringUtils.hasText(nickname)) {
                list.add(criteriaBuilder.like(root.get("nickname"), SystemConstants.generateSqlLike(nickname)));
            }
            MemberEnum memberType = userPageQuery.getMemberType();
            if (!ObjectUtils.isEmpty(memberType)) {
                list.add(criteriaBuilder.equal(root.get("memberType"), memberType));
            }
            SourceEnum sourceType = userPageQuery.getSourceType();
            if (!ObjectUtils.isEmpty(sourceType)) {
                list.add(criteriaBuilder.equal(root.get("sourceType"), sourceType));
            }

            Integer startPurchaseCount = userPageQuery.getStartPurchaseCount();
            Integer endPurchaseCount = userPageQuery.getEndPurchaseCount();
            if (!ObjectUtils.isEmpty(startPurchaseCount) && !ObjectUtils.isEmpty(endPurchaseCount)) {
                list.add(criteriaBuilder.between(root.get("purchaseCount").as(Integer.class), startPurchaseCount, endPurchaseCount));
            }

            if (Objects.nonNull(userPageQuery.getMdseId())) {
                Result<List<MdsePurchaseRecordDTO>> purchaseRecordListResult = mdseFeignClient.getPurchaseRecordByMdseId(userPageQuery.getMdseId());
                if (Result.isSuccess(purchaseRecordListResult)) {
                    purchaseRecordListResult.getData().forEach(purchaseRecord -> userIdSet.add(purchaseRecord.getUserId()));
                }
                userIdSet.add(0L);
            }

            if (!CollectionUtils.isEmpty(userIdSet)) {
                userIdSet.forEach(userIdIn::value);
                list.add(userIdIn);
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return userRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<UserAuthDTO> loadUserByOpenid(String openid) {
        Optional<MallUser> userOptional = userRepository.findByOpenid(openid);
        return getUserAuthDTO(userOptional);
    }

    @Override
    public Optional<UserAuthDTO> loadUserByUsername(String username) {
        Optional<MallUser> userOptional = userRepository.findByUsername(username);
        return getUserAuthDTO(userOptional);
    }

    @Override
    public Optional<UserAuthDTO> loadUserByMobile(String mobile) {
        Optional<MallUser> userOptional = userRepository.findByMobile(mobile);
        return getUserAuthDTO(userOptional);
    }

    @NotNull
    private Optional<UserAuthDTO> getUserAuthDTO(Optional<MallUser> userOptional) {
        if (userOptional.isPresent()) {
            MallUser user = userOptional.get();
            UserAuthDTO userAuthDTO = new UserAuthDTO();
            userAuthDTO.setUserId(user.getUserId());
            userAuthDTO.setOpenId(user.getOpenid());
            userAuthDTO.setUsername(user.getUsername());
            userAuthDTO.setNickname(user.getNickname());
            userAuthDTO.setPassword(user.getPassword());
            userAuthDTO.setStatus(user.getStatus());
            userAuthDTO.setRoles(Lists.newArrayList("USER"));
            List<MallUserRole> userRoleList = userRoleRepository.findAllByUserId(user.getUserId());
            if (!ObjectUtils.isEmpty(userRoleList)) {
                Result<List<RoleInfo>> roleInfoResult = roleFeignClient.getRoleDetailList(userRoleList.stream().map(MallUserRole::getRoleId).map(String::valueOf).collect(Collectors.joining(",")));
                if (Result.isSuccess(roleInfoResult)) {
                    List<RoleInfo> roleInfoList = roleInfoResult.getData();
                    List<String> roleCodeList = roleInfoList.stream().map(RoleInfo::getCode).collect(Collectors.toList());
                    userAuthDTO.setRoles(roleCodeList);
                }
            }

            user.setLoginTime(new Date());
            userRepository.save(user);
            return Optional.of(userAuthDTO);
        }
        return Optional.empty();
    }


}
