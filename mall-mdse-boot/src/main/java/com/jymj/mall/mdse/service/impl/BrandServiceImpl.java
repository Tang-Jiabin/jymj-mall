package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.entity.MdseBrand;
import com.jymj.mall.mdse.repository.BrandRepository;
import com.jymj.mall.mdse.service.BrandService;
import com.jymj.mall.mdse.vo.BrandInfo;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.ShopInfo;
import io.seata.spring.annotation.GlobalTransactional;
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
 * 品牌
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ShopFeignClient shopFeignClient;

    @Override
    public MdseBrand add(BrandDTO dto) {

        verifyBrandName(dto.getName());

        verifyShopId(dto.getShopId());

        MdseBrand brand = new MdseBrand();
        brand.setName(dto.getName());
        brand.setLogo(dto.getLogo());
        brand.setAlias(dto.getAlias());
        brand.setRemarks(dto.getRemarks());
        brand.setShopId(dto.getShopId());
        brand.setDeleted(SystemConstants.DELETED_NO);

        return brandRepository.save(brand);
    }

    private void verifyBrandName(String name) {
        Optional<MdseBrand> brandOptional = brandRepository.findByName(name);

        if (brandOptional.isPresent()) {
            throw new BusinessException("品牌 【" + name + "】 已存在");
        }
    }

    @Override
    public Optional<MdseBrand> update(BrandDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getBrandId())) {
            Optional<MdseBrand> brandOptional = brandRepository.findById(dto.getBrandId());
            if (brandOptional.isPresent()) {
                MdseBrand brand = brandOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName()) && !brand.getName().equals(dto.getName())) {
                    verifyBrandName(dto.getName());
                    brand.setName(dto.getName());
                    update = true;
                }
                if (StringUtils.hasText(dto.getAlias()) && !brand.getAlias().equals(dto.getAlias())) {
                    brand.setAlias(dto.getAlias());
                    update = true;
                }
                if (StringUtils.hasText(dto.getLogo()) && !brand.getLogo().equals(dto.getLogo())) {
                    brand.setLogo(dto.getLogo());
                    update = true;
                }
                if (StringUtils.hasText(dto.getRemarks()) && !brand.getRemarks().equals(dto.getRemarks())) {
                    brand.setRemarks(dto.getRemarks());
                    update = true;
                }
                if (!ObjectUtils.isEmpty(dto.getShopId()) && !brand.getShopId().equals(dto.getShopId())) {
                    verifyShopId(dto.getShopId());
                    brand.setShopId(dto.getShopId());
                    update = true;
                }

                if (update) {
                    return Optional.of(brandRepository.save(brand));
                }
            }
        }


        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseBrand> brandList = brandRepository.findAllById(idList);
            brandRepository.deleteAll(brandList);
        }
    }

    @Override
    public Optional<MdseBrand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public BrandInfo entity2vo(MdseBrand entity) {
        if (entity != null) {
            BrandInfo brandInfo = new BrandInfo();
            brandInfo.setBrandId(entity.getBrandId());
            brandInfo.setName(entity.getName());
            brandInfo.setLogo(entity.getLogo());
            brandInfo.setAlias(entity.getAlias());
            brandInfo.setRemarks(entity.getRemarks());
            return brandInfo;
        }
        return null;
    }

    @Override
    public List<BrandInfo> list2vo(List<MdseBrand> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public List<MdseBrand> findAll() {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (Result.isSuccess(shopListResult)){
            List<ShopInfo> shopInfoList = shopListResult.getData();
            List<Long> shopIdList = shopInfoList.stream().map(ShopInfo::getShopId).collect(Collectors.toList());
            return brandRepository.findAllByShopIdIn(shopIdList);
        }
        return Lists.newArrayList();
    }

    private void verifyShopId(Long shopId) {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (!Result.isSuccess(shopListResult)){
            throw new BusinessException("店铺信息获取失败");
        }
        List<Long> shopIdList = shopListResult.getData().stream().map(ShopInfo::getShopId).collect(Collectors.toList());
        if (!shopIdList.contains(shopId)){
            throw new BusinessException("没有店铺【 "+ shopId+" 】的操作权限");
        }
    }
}
