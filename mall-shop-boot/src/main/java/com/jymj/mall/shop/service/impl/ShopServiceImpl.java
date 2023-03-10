package com.jymj.mall.shop.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.shop.dto.ShopDTO;
import com.jymj.mall.shop.dto.ShopPageQuery;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.entity.MallShop;
import com.jymj.mall.shop.repository.MallShopRepository;
import com.jymj.mall.shop.service.MallService;
import com.jymj.mall.shop.service.ShopService;
import com.jymj.mall.shop.vo.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 店铺（网点）
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final DeptFeignClient deptFeignClient;
    private final MallShopRepository shopRepository;
    private final MallService mallService;
    private final ThreadPoolTaskExecutor executor;

    @Override
    @CacheEvict(value = "mall-shop:shop-list:", key = "'mall-id:'+#dto.mallId")
    public MallShop add(ShopDTO dto) {
        Long deptId = UserUtils.getDeptId();

        Optional<MallDetails> mallOptional = mallService.findById(dto.getMallId());
        MallDetails mallDetails = mallOptional.orElseThrow(() -> new BusinessException("商场不存在"));
        deptId = mallDetails.getDeptId();

        AddDeptDTO addDeptDTO = new AddDeptDTO();
        addDeptDTO.setName(dto.getName());
        addDeptDTO.setParentId(deptId);
        addDeptDTO.setSort(1);
        addDeptDTO.setStatus(SystemConstants.STATUS_OPEN);

        Result<DeptInfo> deptInfoResult = deptFeignClient.add(addDeptDTO);

        if (!Result.isSuccess(deptInfoResult)) {
            throw new BusinessException("[" + dto.getName() + "] 添加失败");
        }
        DeptInfo deptInfo = deptInfoResult.getData();

        MallShop mallShop = new MallShop();
        mallShop.setName(dto.getName());
        mallShop.setAddress(dto.getAddress());
        mallShop.setDirector(dto.getDirector());
        mallShop.setMobile(dto.getMobile());
        mallShop.setStatus(dto.getStatus());
        mallShop.setInBusiness(dto.getInBusiness());
        mallShop.setBusinessStartTime(dto.getBusinessStartTime());
        mallShop.setBusinessEndTime(dto.getBusinessEndTime());
        mallShop.setLongitude(Double.parseDouble(dto.getLongitude()));
        mallShop.setLatitude(Double.parseDouble(dto.getLatitude()));
        mallShop.setMallId(dto.getMallId());
        mallShop.setDeptId(deptInfo.getDeptId());
        mallShop.setDeleted(SystemConstants.DELETED_NO);

        return shopRepository.save(mallShop);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "mall-shop:shop-info:", key = "'shop-id:'+#dto.shopId"),
            @CacheEvict(value = "mall-shop:shop-list:", key = "'mall-id:'+#dto.mallId")
    })
    public Optional<MallShop> update(ShopDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getShopId())) {
            Optional<MallShop> shopOptional = findById(dto.getShopId());
            MallShop mallShop = shopOptional.orElseThrow(() -> new BusinessException("店铺不存在"));
            boolean update = false;

            if (StringUtils.hasText(dto.getName())) {
                mallShop.setName(dto.getName());
                update = true;
            }

            if (StringUtils.hasText(dto.getAddress())) {
                mallShop.setAddress(dto.getAddress());
                update = true;
            }

            if (StringUtils.hasText(dto.getDirector())) {
                mallShop.setDirector(dto.getDirector());
                update = true;
            }

            if (StringUtils.hasText(dto.getMobile())) {
                mallShop.setMobile(dto.getMobile());
                update = true;
            }

            if (dto.getStatus() != null) {
                mallShop.setStatus(dto.getStatus());
                update = true;
            }

            if (dto.getInBusiness() != null) {
                mallShop.setInBusiness(dto.getInBusiness());
                update = true;
            }

            if (dto.getBusinessStartTime() != null) {
                mallShop.setBusinessStartTime(dto.getBusinessStartTime());
                update = true;
            }

            if (dto.getBusinessEndTime() != null) {
                mallShop.setBusinessEndTime(dto.getBusinessEndTime());
                update = true;
            }

            if (StringUtils.hasText(dto.getLongitude())) {
                mallShop.setLongitude(Double.parseDouble(dto.getLongitude()));
                update = true;
            }

            if (StringUtils.hasText(dto.getLatitude())) {
                mallShop.setLatitude(Double.parseDouble(dto.getLatitude()));
                update = true;
            }

            if (dto.getMallId() != null) {
                mallShop.setMallId(dto.getMallId());
                update = true;
            }

            if (update) {
                return Optional.of(shopRepository.save(mallShop));
            }
        }
        return Optional.empty();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "mall-shop:shop-info:", allEntries = true),
            @CacheEvict(value = "mall-shop:shop-list:", allEntries = true)
    })
    public void delete(String ids) {
        List<Long> shopIdList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(shopIdList)) {
            List<MallShop> shopList = shopRepository.findAllById(shopIdList);
            shopRepository.deleteAll(shopList);
        }
    }

    @Override
    public Optional<MallShop> findById(Long id) {
        return shopRepository.findById(id);
    }

    @Override
    public ShopInfo entity2vo(MallShop entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            return getShopInfo(entity);
        }
        return null;
    }


    @Override
    public List<ShopInfo> list2vo(List<MallShop> entityList) {
        List<CompletableFuture<ShopInfo>> futureList = Optional
                .of(entityList)
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
    public Page<MallShop> findPage(ShopPageQuery shopPageQuery) {

        Pageable pageable = PageUtils.getPageable(shopPageQuery);

        Long deptId = UserUtils.getDeptId();
        Assert.notNull(deptId, "部门id不能为空");
        List<DeptInfo> deptInfoList = Lists.newArrayList();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.children(deptId);
        if (Result.isSuccess(deptListResult)) {
            deptInfoList = deptListResult.getData();
        }
        deptInfoList.add(DeptInfo.builder().deptId(0L).build());
        List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());

        Specification<MallShop> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (StringUtils.hasText(shopPageQuery.getName())) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), shopPageQuery.getName() + SystemConstants.SQL_LIKE));
            }

            if (StringUtils.hasText(shopPageQuery.getDirector())) {
                list.add(criteriaBuilder.like(root.get("director").as(String.class), shopPageQuery.getDirector() + SystemConstants.SQL_LIKE));
            }

            if (StringUtils.hasText(shopPageQuery.getMobile())) {
                list.add(criteriaBuilder.like(root.get("mobile").as(String.class), shopPageQuery.getMobile() + SystemConstants.SQL_LIKE));
            }

            if (shopPageQuery.getStatus() != null) {
                list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), shopPageQuery.getStatus()));
            }

            if (Objects.nonNull(shopPageQuery.getMallId())) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Integer.class), shopPageQuery.getMallId()));
            }

            if (!CollectionUtils.isEmpty(deptIdList)) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("deptId").as(Long.class));
                deptIdList.forEach(in::value);
                list.add(in);
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return shopRepository.findAll(spec, pageable);
    }

    @Override
    public List<MallShop> findAllByDeptId(Long deptId) {
        Assert.notNull(deptId, "部门id不能为空");
        Result<List<DeptInfo>> deptListResult = deptFeignClient.children(deptId);
        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            return shopRepository.findAllByDeptIdIn(deptIdList);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<MallShop> findAllById(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return shopRepository.findAllById(idList);


    }

    @Override
    public List<MallShop> findAllByMallId(Long mallId) {

        return shopRepository.findAllByMallId(mallId);
    }

    private ShopInfo getShopInfo(MallShop entity) {

        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setShopId(entity.getShopId());
        shopInfo.setName(entity.getName());
        shopInfo.setAddress(entity.getAddress());
        shopInfo.setDirector(entity.getDirector());
        shopInfo.setMobile(entity.getMobile());
        shopInfo.setStatus(entity.getStatus());
        shopInfo.setInBusiness(entity.getInBusiness());
        shopInfo.setBusinessStartTime(entity.getBusinessStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        shopInfo.setBusinessEndTime(entity.getBusinessEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        shopInfo.setLongitude(entity.getLongitude());
        shopInfo.setLatitude(entity.getLatitude());

        shopInfo.setPosition(new GeoPoint(entity.getLatitude(), entity.getLongitude()));
        return shopInfo;
    }
}


