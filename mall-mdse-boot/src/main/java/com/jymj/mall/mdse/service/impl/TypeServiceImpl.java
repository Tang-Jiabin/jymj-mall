package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.TypeDTO;
import com.jymj.mall.mdse.entity.MdseType;
import com.jymj.mall.mdse.repository.MdseTypeRepository;
import com.jymj.mall.mdse.service.TypeService;
import com.jymj.mall.mdse.vo.TypeInfo;
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
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {

    private final MdseTypeRepository typeRepository;
    private final ShopFeignClient shopFeignClient;


    @Override
    public MdseType add(TypeDTO dto) {

        verifyShopId(dto.getShopId());

        MdseType mdseType = new MdseType();
        mdseType.setName(dto.getName());
        mdseType.setRemarks(dto.getRemarks());
        mdseType.setShopId(dto.getShopId());
        mdseType.setDeleted(SystemConstants.DELETED_NO);

        return typeRepository.save(mdseType);
    }

    @Override
    public Optional<MdseType> update(TypeDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getTypeId())) {
            Optional<MdseType> typeOptional = typeRepository.findById(dto.getTypeId());
            if (typeOptional.isPresent()) {
                MdseType mdseType = typeOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName())) {
                    mdseType.setName(dto.getName());
                    update = true;
                }

                if (StringUtils.hasText(dto.getRemarks())) {
                    mdseType.setRemarks(dto.getRemarks());
                    update = true;
                }

                if (!ObjectUtils.isEmpty(dto.getShopId())) {
                    verifyShopId(dto.getShopId());
                    mdseType.setShopId(dto.getShopId());
                    update = true;
                }
                if (update) {
                    return Optional.of(typeRepository.save(mdseType));
                }

            }

        }


        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseType> mdseTypeList = typeRepository.findAllById(idList);
            typeRepository.deleteAll(mdseTypeList);
        }
    }

    @Override
    public Optional<MdseType> findById(Long id) {
        return typeRepository.findById(id);
    }

    @Override
    public TypeInfo entity2vo(MdseType entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            TypeInfo typeInfo = new TypeInfo();
            typeInfo.setTypeId(entity.getTypeId());
            typeInfo.setName(entity.getName());
            typeInfo.setRemarks(entity.getRemarks());
            typeInfo.setShopId(entity.getShopId());
            return typeInfo;
        }
        return null;
    }

    @Override
    public List<TypeInfo> list2vo(List<MdseType> entityList) {
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
    public List<MdseType> findAllByAuth() {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (Result.isSuccess(shopListResult)){
            List<ShopInfo> shopInfoList = shopListResult.getData();
            List<Long> shopIdList = shopInfoList.stream().map(ShopInfo::getShopId).collect(Collectors.toList());
            return typeRepository.findAllByShopIdIn(shopIdList);
        }
        return Lists.newArrayList();
    }
}
