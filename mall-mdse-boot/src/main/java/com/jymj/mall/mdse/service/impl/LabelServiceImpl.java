package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.LabelDTO;
import com.jymj.mall.mdse.entity.MallMdseLabelMap;
import com.jymj.mall.mdse.entity.MdseLabel;
import com.jymj.mall.mdse.repository.MdseLabelMapRepository;
import com.jymj.mall.mdse.repository.MdseLabelRepository;
import com.jymj.mall.mdse.service.LabelService;
import com.jymj.mall.mdse.vo.LabelInfo;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final ShopFeignClient shopFeignClient;

    private final MdseLabelRepository labelRepository;
    private final MdseLabelMapRepository labelMapRepository;

    @Override
    public MdseLabel add(LabelDTO dto) {

        verifyShopId(dto.getShopId());

        MdseLabel label = new MdseLabel();
        label.setName(dto.getName());
        label.setRemarks(dto.getRemarks());
        label.setShopId(dto.getShopId());
        label.setDeleted(SystemConstants.DELETED_NO);

        return labelRepository.save(label);

    }

    @Override
    public Optional<MdseLabel> update(LabelDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getLabelId())) {
            Optional<MdseLabel> labelOptional = labelRepository.findById(dto.getLabelId());
            if (labelOptional.isPresent()) {
                MdseLabel mdseLabel = labelOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName())) {
                    mdseLabel.setName(dto.getName());
                    update = true;
                }

                if (StringUtils.hasText(dto.getRemarks())) {
                    mdseLabel.setRemarks(dto.getRemarks());
                    update = true;
                }

                if (!ObjectUtils.isEmpty(dto.getShopId())) {
                    verifyShopId(dto.getShopId());
                    mdseLabel.setShopId(dto.getShopId());
                    update = true;
                }
                if (update) {
                    return Optional.of(labelRepository.save(mdseLabel));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseLabel> labelList = labelRepository.findAllById(idList);
            labelRepository.deleteAll(labelList);
        }
    }

    @Override
    public Optional<MdseLabel> findById(Long id) {
        return labelRepository.findById(id);
    }

    @Override
    public LabelInfo entity2vo(MdseLabel entity) {

        if (!ObjectUtils.isEmpty(entity)) {
            LabelInfo labelInfo = new LabelInfo();
            labelInfo.setLabelId(entity.getLabelId());
            labelInfo.setName(entity.getName());
            labelInfo.setRemarks(entity.getRemarks());
            labelInfo.setShopId(entity.getShopId());
            return labelInfo;

        }
        return null;
    }

    @Override
    public List<LabelInfo> list2vo(List<MdseLabel> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    private void verifyShopId(Long shopId) {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (!Result.isSuccess(shopListResult)) {
            throw new BusinessException("店铺信息获取失败");
        }
        List<Long> shopIdList = shopListResult.getData().stream().map(ShopInfo::getShopId).collect(Collectors.toList());
        if (!shopIdList.contains(shopId)) {
            throw new BusinessException("没有店铺【 " + shopId + " 】的操作权限");
        }
    }

    @Override
    public List<MdseLabel> findAllById(List<Long> idList) {
        return labelRepository.findAllById(idList);
    }

    @Override
    public void addMdseLabelMap(Long mdseId, List<MdseLabel> labelList) {
        for (MdseLabel mdseLabel : labelList) {
            MallMdseLabelMap labelMap = new MallMdseLabelMap();
            labelMap.setMdseId(mdseId);
            labelMap.setLabelId(mdseLabel.getLabelId());
            labelMap.setDeleted(SystemConstants.DELETED_NO);
            labelMapRepository.save(labelMap);
        }
    }

    @Override
    public List<MdseLabel> findAllByMdseId(Long mdseId) {
        List<MallMdseLabelMap> mdseLabelMapList = labelMapRepository.findAllByMdseId(mdseId);
        return labelRepository.findAllById(
                mdseLabelMapList.stream().map(MallMdseLabelMap::getLabelId).collect(Collectors.toList())
        );
    }

    @Override
    public List<MdseLabel> findAllByAuth() {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (Result.isSuccess(shopListResult)) {
            List<ShopInfo> shopInfoList = shopListResult.getData();
            List<Long> shopIdList = shopInfoList.stream().map(ShopInfo::getShopId).collect(Collectors.toList());
            return labelRepository.findAllByShopIdIn(shopIdList);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<MallMdseLabelMap> findAllMdseLabelByLabelId(Long labelId) {

        return labelMapRepository.findAllByLabelId(labelId);
    }
}
