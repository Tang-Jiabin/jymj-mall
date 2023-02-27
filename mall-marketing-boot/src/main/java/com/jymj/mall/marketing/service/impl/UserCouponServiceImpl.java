package com.jymj.mall.marketing.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.dto.UserCouponDTO;
import com.jymj.mall.marketing.entity.MallCoupon;
import com.jymj.mall.marketing.entity.UserCoupon;
import com.jymj.mall.marketing.repository.CouponRepository;
import com.jymj.mall.marketing.repository.UserCouponRepository;
import com.jymj.mall.marketing.service.UserCouponService;
import com.jymj.mall.marketing.vo.UserCouponInfo;
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

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 用户优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {


    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final ThreadPoolTaskExecutor executor;


    @Override
    public UserCoupon add(UserCouponDTO dto) {

        Assert.notNull(dto.getCouponTemplateId(), "优惠券模板ID不能为空");
        Assert.notNull(dto.getUserId(), "用户ID不能为空");

        UserCoupon entity = new UserCoupon();
        entity.setCouponTemplateId(dto.getCouponTemplateId());
        entity.setUserId(dto.getUserId());

        Optional<MallCoupon> mallCouponOptional = couponRepository.findById(dto.getCouponTemplateId());

        MallCoupon mallCoupon = mallCouponOptional.orElseThrow(() -> new RuntimeException("优惠券模板不存在"));

        entity.setMallId(mallCoupon.getMallId());
        entity.setName(mallCoupon.getName());
        entity.setType(mallCoupon.getType());
        entity.setStatus(dto.getStatus());
        entity.setFullAmount(mallCoupon.getFullAmount());
        entity.setAmount(mallCoupon.getAmount());
        entity.setEffectiveTime(mallCoupon.getEffectiveTime());
        entity.setInvalidTime(mallCoupon.getInvalidTime());
        entity.setShare(mallCoupon.getShare());
        entity.setDescription(mallCoupon.getDescription());
        entity.setPicUrl(mallCoupon.getPicUrl());
        entity.setProductType(mallCoupon.getProductType());
        entity.setProductIds(mallCoupon.getProductIds());
        entity.setNotProductIds(mallCoupon.getNotProductIds());
        entity.setProductCategoryIds(mallCoupon.getProductCategoryIds());
        entity.setNotProductCategoryIds(mallCoupon.getNotProductCategoryIds());
        entity.setDeleted(SystemConstants.DELETED_NO);
        return userCouponRepository.save(entity);
    }

    @Override
    public Optional<UserCoupon> update(UserCouponDTO dto) {

        Assert.notNull(dto.getCouponId(), "id不能为空");
        Assert.notNull(dto.getUserId(), "用户id不能为空");

        Optional<UserCoupon> optional = findById(dto.getCouponId());

        UserCoupon userCoupon = optional.orElseThrow(() -> new RuntimeException("用户优惠券不存在"));

        userCoupon.setStatus(dto.getStatus());

        return Optional.of(userCouponRepository.save(userCoupon));

    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<UserCoupon> mdseList = userCouponRepository.findAllById(idList);
            if (!CollectionUtils.isEmpty(mdseList)) {
                userCouponRepository.deleteAll(mdseList);
            }
        }
    }

    @Override
    public Optional<UserCoupon> findById(Long id) {
        return userCouponRepository.findById(id);
    }

    @Override
    public UserCouponInfo entity2vo(UserCoupon entity) {
        UserCouponInfo vo = new UserCouponInfo();
        vo.setCouponId(entity.getCouponId());
        vo.setMallId(entity.getMallId());
        vo.setName(entity.getName());
        vo.setType(entity.getType());
        vo.setStatus(entity.getStatus());
        vo.setFullAmount(entity.getFullAmount());
        vo.setAmount(entity.getAmount());
        vo.setEffectiveTime(entity.getEffectiveTime());
        vo.setInvalidTime(entity.getInvalidTime());
        vo.setShare(entity.getShare());
        vo.setDescription(entity.getDescription());
        vo.setPicUrl(entity.getPicUrl());
        vo.setProductType(entity.getProductType());
        vo.setProductIds(entity.getProductIds());
        vo.setNotProductIds(entity.getNotProductIds());
        vo.setProductCategoryIds(entity.getProductCategoryIds());
        vo.setNotProductCategoryIds(entity.getNotProductCategoryIds());
        return vo;
    }

    @Override
    public List<UserCouponInfo> list2vo(List<UserCoupon> entityList) {
        List<CompletableFuture<UserCouponInfo>> futureList = Optional.of(entityList)
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
    public Page<UserCoupon> findPage(CouponPageQuery couponPageQuery) {

        Pageable pageable = PageUtils.getPageable(couponPageQuery);

        Specification<UserCoupon> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();
            Predicate[] p = new Predicate[list.size()];
            if (!ObjectUtils.isEmpty(couponPageQuery.getUserId())) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), couponPageQuery.getUserId()));
            }
            if (!ObjectUtils.isEmpty(couponPageQuery.getStatus())) {
                list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), couponPageQuery.getStatus()));
            }

            return criteriaBuilder.and(list.toArray(p));
        };

        return userCouponRepository.findAll(spec, pageable);
    }

    @Override
    public List<UserCoupon> findByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            if (idList.size() == 1) {
                Optional<UserCoupon> optional = userCouponRepository.findById(idList.get(0));
                return optional.map(Lists::newArrayList).orElse(Lists.newArrayList());
            }else {
                return userCouponRepository.findAllById(idList);
            }
        }
        return Lists.newArrayList();
    }
}
