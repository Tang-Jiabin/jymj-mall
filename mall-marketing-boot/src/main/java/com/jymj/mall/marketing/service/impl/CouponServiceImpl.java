package com.jymj.mall.marketing.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.enums.CouponStateEnum;
import com.jymj.mall.common.enums.CouponTypeEnum;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.marketing.dto.CouponDTO;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.entity.MallCoupon;
import com.jymj.mall.marketing.entity.UserCoupon;
import com.jymj.mall.marketing.repository.CouponRepository;
import com.jymj.mall.marketing.service.CouponService;
import com.jymj.mall.marketing.service.UserCouponService;
import com.jymj.mall.marketing.vo.CouponInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final ThreadPoolTaskExecutor executor;
    private final AdminFeignClient adminFeignClient;
    private final UserCouponService userCouponService;

    @Override
    public MallCoupon add(CouponDTO dto) {

        MallCoupon coupon = new MallCoupon();

        Long adminId = UserUtils.getAdminId();
        Result<AdminInfo> adminInfoResult = adminFeignClient.getAdminById(adminId);
        if (Result.isSuccess(adminInfoResult)) {
            AdminInfo adminInfo = adminInfoResult.getData();
            coupon.setMallId(adminInfo.getMallId());
        }

        coupon.setName(dto.getName());
        coupon.setType(dto.getType());
        coupon.setFullAmount(dto.getFullAmount());
        coupon.setAmount(dto.getAmount());
        coupon.setQuantity(dto.getQuantity());
        coupon.setEffectiveType(dto.getEffectiveType());
        coupon.setEffectiveTime(dto.getEffectiveTime());
        coupon.setEffectiveDays(dto.getEffectiveDays());
        coupon.setInvalidType(dto.getInvalidType());
        coupon.setInvalidTime(dto.getInvalidTime());
        coupon.setInvalidDays(dto.getInvalidDays());
        coupon.setReceiveType(dto.getReceiveType());
        coupon.setReceiveCount(dto.getReceiveCount());
        coupon.setShare(dto.getShare());
        coupon.setDescription(dto.getDescription());
        coupon.setStatus(dto.getStatus());
        coupon.setPicUrl(dto.getPicUrl());
        coupon.setSort(dto.getSort());
        coupon.setRemark(dto.getRemark());
        coupon.setProductType(dto.getProductType());
        coupon.setProductIds(dto.getProductIds());
        coupon.setNotProductIds(dto.getNotProductIds());
        coupon.setProductCategoryIds(dto.getProductCategoryIds());
        coupon.setNotProductCategoryIds(dto.getNotProductCategoryIds());
        coupon.setDeleted(SystemConstants.DELETED_NO);

        return couponRepository.save(coupon);
    }

    @Override
    public Optional<MallCoupon> update(CouponDTO dto) {

        Assert.notNull(dto.getCouponId(), "id不能为空");

        Optional<MallCoupon> couponOptional = findById(dto.getCouponId());

        MallCoupon coupon = couponOptional.orElseThrow(() -> new RuntimeException("优惠券不存在"));

        boolean isUpdate = false;

        if (!ObjectUtils.isEmpty(dto.getName())) {
            isUpdate = true;
            coupon.setName(dto.getName());
        }
        if (!ObjectUtils.isEmpty(dto.getType())) {
            isUpdate = true;
            coupon.setType(dto.getType());
        }
        if (!ObjectUtils.isEmpty(dto.getFullAmount())) {
            isUpdate = true;
            coupon.setFullAmount(dto.getFullAmount());
        }
        if (!ObjectUtils.isEmpty(dto.getAmount())) {
            isUpdate = true;
            coupon.setAmount(dto.getAmount());
        }
        if (!ObjectUtils.isEmpty(dto.getQuantity())) {
            isUpdate = true;
            coupon.setQuantity(dto.getQuantity());
        }
        if (!ObjectUtils.isEmpty(dto.getEffectiveType())) {
            isUpdate = true;
            coupon.setEffectiveType(dto.getEffectiveType());
        }
        if (!ObjectUtils.isEmpty(dto.getEffectiveTime())) {
            isUpdate = true;
            coupon.setEffectiveTime(dto.getEffectiveTime());
        }
        if (!ObjectUtils.isEmpty(dto.getEffectiveDays())) {
            isUpdate = true;
            coupon.setEffectiveDays(dto.getEffectiveDays());
        }
        if (!ObjectUtils.isEmpty(dto.getInvalidType())) {
            isUpdate = true;
            coupon.setInvalidType(dto.getInvalidType());
        }
        if (!ObjectUtils.isEmpty(dto.getInvalidTime())) {
            isUpdate = true;
            coupon.setInvalidTime(dto.getInvalidTime());
        }
        if (!ObjectUtils.isEmpty(dto.getInvalidDays())) {
            isUpdate = true;
            coupon.setInvalidDays(dto.getInvalidDays());
        }
        if (!ObjectUtils.isEmpty(dto.getReceiveType())) {
            isUpdate = true;
            coupon.setReceiveType(dto.getReceiveType());
        }
        if (!ObjectUtils.isEmpty(dto.getReceiveCount())) {
            isUpdate = true;
            coupon.setReceiveCount(dto.getReceiveCount());
        }
        if (!ObjectUtils.isEmpty(dto.getShare())) {
            isUpdate = true;
            coupon.setShare(dto.getShare());
        }
        if (!ObjectUtils.isEmpty(dto.getDescription())) {
            isUpdate = true;
            coupon.setDescription(dto.getDescription());
        }
        if (!ObjectUtils.isEmpty(dto.getStatus())) {
            isUpdate = true;
            coupon.setStatus(dto.getStatus());
        }
        if (!ObjectUtils.isEmpty(dto.getPicUrl())) {
            isUpdate = true;
            coupon.setPicUrl(dto.getPicUrl());
        }
        if (!ObjectUtils.isEmpty(dto.getSort())) {
            isUpdate = true;
            coupon.setSort(dto.getSort());
        }
        if (!ObjectUtils.isEmpty(dto.getRemark())) {
            isUpdate = true;
            coupon.setRemark(dto.getRemark());
        }
        if (!ObjectUtils.isEmpty(dto.getProductType())) {
            isUpdate = true;
            coupon.setProductType(dto.getProductType());
        }
        if (!ObjectUtils.isEmpty(dto.getProductIds())) {
            isUpdate = true;
            coupon.setProductIds(dto.getProductIds());
        }
        if (!ObjectUtils.isEmpty(dto.getNotProductIds())) {
            isUpdate = true;
            coupon.setNotProductIds(dto.getNotProductIds());
        }
        if (!ObjectUtils.isEmpty(dto.getProductCategoryIds())) {
            isUpdate = true;
            coupon.setProductCategoryIds(dto.getProductCategoryIds());
        }
        if (!ObjectUtils.isEmpty(dto.getNotProductCategoryIds())) {
            isUpdate = true;
            coupon.setNotProductCategoryIds(dto.getNotProductCategoryIds());
        }

        if (isUpdate) {
            return Optional.of(couponRepository.save(coupon));
        }

        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallCoupon> mdseList = couponRepository.findAllById(idList);
            if (!CollectionUtils.isEmpty(mdseList)) {
                couponRepository.deleteAll(mdseList);
            }
        }
    }

    @Override
    public Optional<MallCoupon> findById(Long id) {
        return couponRepository.findById(id);
    }

    @Override
    public CouponInfo entity2vo(MallCoupon entity) {
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        CouponInfo vo = new CouponInfo();
        vo.setCouponId(entity.getCouponId());
        vo.setMallId(entity.getMallId());
        vo.setName(entity.getName());
        vo.setType(entity.getType());
        vo.setFullAmount(entity.getFullAmount());
        vo.setAmount(entity.getAmount());
        vo.setQuantity(entity.getQuantity());
        vo.setEffectiveType(entity.getEffectiveType());
        vo.setEffectiveTime(entity.getEffectiveTime());
        vo.setEffectiveDays(entity.getEffectiveDays());
        vo.setInvalidType(entity.getInvalidType());
        vo.setInvalidTime(entity.getInvalidTime());
        vo.setInvalidDays(entity.getInvalidDays());
        vo.setReceiveType(entity.getReceiveType());
        vo.setReceiveCount(entity.getReceiveCount());
        vo.setShare(entity.getShare());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setPicUrl(entity.getPicUrl());
        vo.setSort(entity.getSort());
        vo.setRemark(entity.getRemark());
        vo.setProductType(entity.getProductType());
        vo.setProductIds(entity.getProductIds());
        vo.setNotProductIds(entity.getNotProductIds());
        vo.setProductCategoryIds(entity.getProductCategoryIds());
        vo.setNotProductCategoryIds(entity.getNotProductCategoryIds());
        List<UserCoupon> userCouponList = userCouponService.findAllByCouponTemplateId(entity.getCouponId());
        //已领取
        vo.setReceived(userCouponList.size());
        //未领取
        vo.setUnreceived(entity.getQuantity() - userCouponList.size());
        //按照状态分类
        Map<CouponStateEnum, List<UserCoupon>> userCouponMap = userCouponList.stream().collect(Collectors.groupingBy(UserCoupon::getStatus));
        //未使用
        vo.setUnused(userCouponMap.getOrDefault(CouponStateEnum.NORMAL, Collections.emptyList()).size());
        //已使用
        vo.setUsed(userCouponMap.getOrDefault(CouponStateEnum.USED, Collections.emptyList()).size());
        //已过期
        vo.setExpired(userCouponMap.getOrDefault(CouponStateEnum.EXPIRED, Collections.emptyList()).size());
        return vo;
    }

    @Override
    public List<CouponInfo> list2vo(List<MallCoupon> entityList) {
        List<CompletableFuture<CouponInfo>> futureList = Optional.of(entityList)
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
    public Page<MallCoupon> findPage(CouponPageQuery couponPageQuery) {

        Pageable pageable = PageUtils.getPageable(couponPageQuery);

        Specification<MallCoupon> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();
            Predicate[] p = new Predicate[list.size()];
            if (!ObjectUtils.isEmpty(couponPageQuery.getUserId())) {
                List<Long> couponIdList = userCouponService.findByUserId(couponPageQuery.getUserId()).stream().map(UserCoupon::getCouponTemplateId).collect(Collectors.toList());
                list.add(criteriaBuilder.in(root.get("couponId")).value(couponIdList));
            }
            if (StringUtils.hasText(couponPageQuery.getCouponName())) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), SystemConstants.generateSqlLike(couponPageQuery.getCouponName())));
            }
            if (!ObjectUtils.isEmpty(couponPageQuery.getStatus())) {
                list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), couponPageQuery.getStatus()));
            }
            if (!ObjectUtils.isEmpty(couponPageQuery.getType())) {
                list.add(criteriaBuilder.equal(root.get("type").as(CouponTypeEnum.class), couponPageQuery.getType()));
            }
            if (!ObjectUtils.isEmpty(couponPageQuery.getEffectiveTime()) && !ObjectUtils.isEmpty(couponPageQuery.getInvalidTime())) {
                list.add(criteriaBuilder.between(root.get("effectiveTime").as(Date.class), couponPageQuery.getEffectiveTime(), couponPageQuery.getInvalidTime()));
            }
            if (!ObjectUtils.isEmpty(couponPageQuery.getMdseId())) {
                list.add(criteriaBuilder.or(criteriaBuilder.or(criteriaBuilder.equal(root.get("productType").as(Integer.class), 1)), criteriaBuilder.notLike(root.get("notProductIds").as(String.class), "%" + couponPageQuery.getMdseId() + "%"), criteriaBuilder.like(root.get("productIds").as(String.class), "%" + couponPageQuery.getMdseId() + "%")));
            }
            if (!ObjectUtils.isEmpty(couponPageQuery.getMdseCategoryId())) {
                list.add(criteriaBuilder.or(criteriaBuilder.or(criteriaBuilder.equal(root.get("productType").as(Integer.class), 1), criteriaBuilder.notLike(root.get("notProductCategoryIds").as(String.class), "%" + couponPageQuery.getMdseCategoryId() + "%")), criteriaBuilder.like(root.get("productCategoryIds").as(String.class), "%" + couponPageQuery.getMdseCategoryId() + "%")));
            }

            return criteriaBuilder.and(list.toArray(p));
        };

        return couponRepository.findAll(spec, pageable);
    }
}
