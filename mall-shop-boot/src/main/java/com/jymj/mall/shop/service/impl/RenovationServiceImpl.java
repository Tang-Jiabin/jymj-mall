package com.jymj.mall.shop.service.impl;

import cn.hutool.extra.validation.ValidationUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.shop.dto.RenovationDTO;
import com.jymj.mall.shop.entity.ShopRenovation;
import com.jymj.mall.shop.repository.RenovationRepository;
import com.jymj.mall.shop.service.RenovationService;
import com.jymj.mall.shop.vo.RenovationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.ValidationUtils;

import javax.validation.Validation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RenovationServiceImpl implements RenovationService {

    private final RenovationRepository renovationRepository;

    private final ThreadPoolTaskExecutor executor;

    @Override
    public ShopRenovation add(RenovationDTO dto) {

        Assert.notNull(dto.getMallId(), "商城id不能为空");

        ShopRenovation renovation = new ShopRenovation();
        renovation.setParentId(Objects.nonNull(dto.getParentId()) ? dto.getParentId() : 0L);
        renovation.setMallId(dto.getMallId());
        renovation.setTitle(dto.getTitle());
        renovation.setStyle(dto.getStyle());
        renovation.setData(dto.getData());
        renovation.setComponents(dto.getComponents());
        renovation.setStatus(Objects.nonNull(dto.getStatus()) ? dto.getStatus() : SystemConstants.STATUS_CLOSE);
        renovation.setDeleted(SystemConstants.DELETED_NO);

        return renovationRepository.save(renovation);
    }

    @Override
    public Optional<ShopRenovation> update(RenovationDTO dto) {

        Assert.notNull(dto.getRenovationId(), "id不能为空");

        Optional<ShopRenovation> renovationOptional = findById(dto.getRenovationId());

        if (renovationOptional.isPresent()) {
            ShopRenovation shopRenovation = renovationOptional.get();
            if (Objects.nonNull(dto.getParentId())) {
                shopRenovation.setParentId(dto.getParentId());
            }
            if (Objects.nonNull(dto.getMallId())) {
                shopRenovation.setMallId(dto.getMallId());
            }
            if (StringUtils.hasText(dto.getTitle())) {
                shopRenovation.setTitle(dto.getTitle());
            }
            if (StringUtils.hasText(dto.getStyle())) {
                shopRenovation.setStyle(dto.getStyle());
            }
            if (StringUtils.hasText(dto.getData())) {
                shopRenovation.setData(dto.getData());
            }
            if (StringUtils.hasText(dto.getComponents())) {
                shopRenovation.setComponents(dto.getComponents());
            }
            if (Objects.nonNull(dto.getStatus())) {
                shopRenovation.setStatus(dto.getStatus());
            }
            return Optional.of(renovationRepository.save(shopRenovation));
        }
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<ShopRenovation> labelList = renovationRepository.findAllById(idList);
            renovationRepository.deleteAll(labelList);
        }
    }

    @Override
    public Optional<ShopRenovation> findById(Long id) {
        return renovationRepository.findById(id);
    }

    @Override
    public RenovationInfo entity2vo(ShopRenovation entity) {
        if (Objects.nonNull(entity)) {
            RenovationInfo info = new RenovationInfo();
            info.setRenovationId(entity.getRenovationId());
            info.setMallId(entity.getMallId());
            info.setTitle(entity.getTitle());
            info.setStyle(entity.getStyle());
            info.setData(entity.getData());
            info.setComponents(entity.getComponents());
            info.setStatus(entity.getStatus());
            List<ShopRenovation> renovationList = renovationRepository.findAllByParentId(entity.getParentId());
            if (!CollectionUtils.isEmpty(renovationList)) {
                List<RenovationInfo> renovationInfos = list2vo(renovationList);
                info.setSubEntry(renovationInfos);
            }

            return info;
        }
        return null;
    }

    @Override
    public List<RenovationInfo> list2vo(List<ShopRenovation> entityList) {
        List<CompletableFuture<RenovationInfo>> futureList = Optional.of(entityList)
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
    public List<ShopRenovation> findAllByMallId(Long mallId) {
        return renovationRepository.findAllByMallId(mallId);
    }
}
