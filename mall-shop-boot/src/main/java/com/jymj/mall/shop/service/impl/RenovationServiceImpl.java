package com.jymj.mall.shop.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.shop.dto.RenovationDTO;
import com.jymj.mall.shop.dto.RenovationPageQuery;
import com.jymj.mall.shop.dto.RenovationStatusDTO;
import com.jymj.mall.shop.entity.ShopRenovation;
import com.jymj.mall.shop.repository.RenovationRepository;
import com.jymj.mall.shop.service.RenovationService;
import com.jymj.mall.shop.vo.RenovationInfo;
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
        renovation.setHomePage(dto.getHomePage());
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
            if (Objects.nonNull(dto.getHomePage())) {
                shopRenovation.setHomePage(dto.getHomePage());
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
            info.setHomePage(entity.getHomePage());
            info.setCreateTime(entity.getCreateTime());
            info.setUpdateTime(entity.getUpdateTime());
            List<ShopRenovation> renovationList = renovationRepository.findAllByParentId(entity.getRenovationId());
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
    public List<ShopRenovation> findAllByMallIdAndStatus(Long mallId, Integer status) {
        return renovationRepository.findAllByMallIdAndStatus(mallId, status);
    }

    @Override
    public void updateStatus(RenovationStatusDTO renovationStatusDTO) {
        Assert.notNull(renovationStatusDTO.getIdList(), "id不能为空");
        Assert.notNull(renovationStatusDTO.getStatus(), "状态不能为空");
        List<ShopRenovation> renovationList = renovationRepository.findAllById(renovationStatusDTO.getIdList());
        if (!CollectionUtils.isEmpty(renovationList)) {
            renovationList.forEach(renovation -> renovation.setStatus(renovationStatusDTO.getStatus()));
            renovationRepository.saveAll(renovationList);
        }
    }

    @Override
    public void updateHome(RenovationDTO renovationDTO) {
        Assert.notNull(renovationDTO.getRenovationId(), "id不能为空");
        Assert.notNull(renovationDTO.getMallId(), "商城id不能为空");
        List<ShopRenovation> renovationList = renovationRepository.findAllByMallId(renovationDTO.getMallId());
        renovationList.forEach(renovation -> renovation.setHomePage(renovation.getRenovationId().equals(renovationDTO.getRenovationId()) ? SystemConstants.HOME_PAGE_YES : SystemConstants.HOME_PAGE_NO));
        renovationRepository.saveAll(renovationList);
    }

    @Override
    public List<ShopRenovation> findAllByMallId(Long mallId) {
        return renovationRepository.findAllByMallId(mallId);
    }

    @Override
    public Page<ShopRenovation> findPage(RenovationPageQuery renovationPageQuery) {
        Pageable pageable = PageUtils.getPageable(renovationPageQuery);

        return renovationRepository.findAll((Specification<ShopRenovation>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (Objects.nonNull(renovationPageQuery.getMallId())) {
                predicates.add(criteriaBuilder.equal(root.get("mallId"), renovationPageQuery.getMallId()));
            }
            if (Objects.nonNull(renovationPageQuery.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), renovationPageQuery.getStatus()));
            }
            if (Objects.nonNull(renovationPageQuery.getHomePage())) {
                predicates.add(criteriaBuilder.equal(root.get("homePage"), renovationPageQuery.getHomePage()));
            }
            if (StringUtils.hasText(renovationPageQuery.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), SystemConstants.generateSqlLike(renovationPageQuery.getTitle())));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
