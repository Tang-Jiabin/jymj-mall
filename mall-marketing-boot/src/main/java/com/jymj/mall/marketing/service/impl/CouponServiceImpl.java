package com.jymj.mall.marketing.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.marketing.dto.CouponDTO;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.entity.MallCoupon;
import com.jymj.mall.marketing.repository.CouponRepository;
import com.jymj.mall.marketing.service.CouponService;
import com.jymj.mall.marketing.vo.CouponInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
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

    @Override
    public MallCoupon add(CouponDTO dto) {
        return null;
    }

    @Override
    public Optional<MallCoupon> update(CouponDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallCoupon> mdseList = couponRepository.findAllById(idList);
            if (!CollectionUtils.isEmpty(mdseList)){
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
        return null;
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
            return criteriaBuilder.and(list.toArray(p));
        };

        return couponRepository.findAll(spec, pageable);
    }
}
