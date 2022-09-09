package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.entity.MdseBrand;
import com.jymj.mall.mdse.repository.MdseBrandRepository;
import com.jymj.mall.mdse.service.BrandService;
import com.jymj.mall.mdse.vo.BrandInfo;
import com.jymj.mall.shop.api.MallFeignClient;
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

    private final MdseBrandRepository mdseBrandRepository;
    private final MallFeignClient mallFeignClient;
    private final DeptFeignClient deptFeignClient;

    @Override
    public MdseBrand add(BrandDTO dto) {


        MdseBrand brand = new MdseBrand();
        brand.setName(dto.getName());
        brand.setLogo(dto.getLogo());
        brand.setAlias(dto.getAlias());
        brand.setRemarks(dto.getRemarks());
        brand.setMallId(dto.getMallId());
        brand.setDeleted(SystemConstants.DELETED_NO);

        return mdseBrandRepository.save(brand);
    }


    @Override
    public Optional<MdseBrand> update(BrandDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getBrandId())) {
            Optional<MdseBrand> brandOptional = mdseBrandRepository.findById(dto.getBrandId());
            if (brandOptional.isPresent()) {
                MdseBrand brand = brandOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName()) && !brand.getName().equals(dto.getName())) {
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
                if (!ObjectUtils.isEmpty(dto.getMallId()) && !brand.getMallId().equals(dto.getMallId())) {

                    brand.setMallId(dto.getMallId());
                    update = true;
                }

                if (update) {
                    return Optional.of(mdseBrandRepository.save(brand));
                }
            }
        }


        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseBrand> brandList = mdseBrandRepository.findAllById(idList);
            mdseBrandRepository.deleteAll(brandList);
        }
    }

    @Override
    public Optional<MdseBrand> findById(Long id) {
        return mdseBrandRepository.findById(id);
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
    public List<MdseBrand> findAllByMallId(Long mallId) {
        if (mallId != null) {

            return mdseBrandRepository.findAllByMallId(mallId);
        }
        return mdseBrandRepository.findAll();
    }


}
