package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.MfgDTO;
import com.jymj.mall.mdse.entity.MdseMfg;
import com.jymj.mall.mdse.repository.MfgRepository;
import com.jymj.mall.mdse.service.MfgService;
import com.jymj.mall.mdse.vo.MfgInfo;
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
 * 厂家
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Service
@RequiredArgsConstructor
public class MfgServiceImpl implements MfgService {

    private final MfgRepository mfgRepository;
    private final ShopFeignClient shopFeignClient;

    @Override
    public MdseMfg add(MfgDTO dto) {

        verifyMFGName(dto.getName());

        verifyShopId(dto.getShopId());

        MdseMfg mdseMfg = new MdseMfg();
        mdseMfg.setName(dto.getName());
        mdseMfg.setLogo(dto.getLogo());
        mdseMfg.setShopId(dto.getShopId());
        mdseMfg.setAddress(dto.getAddress());
        mdseMfg.setRemarks(dto.getRemarks());
        mdseMfg.setDeleted(SystemConstants.DELETED_NO);

        return mfgRepository.save(mdseMfg);
    }

    private void verifyMFGName(String name) {
        Optional<MdseMfg> mfgOptional = mfgRepository.findByName(name);

        if (mfgOptional.isPresent()) {
            throw new BusinessException("厂家 【" + name + "】 已存在");
        }
    }

    @Override
    public Optional<MdseMfg> update(MfgDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getMfgId())) {
            Optional<MdseMfg> mfgOptional = mfgRepository.findById(dto.getMfgId());
            if (mfgOptional.isPresent()) {
                MdseMfg mdseMfg = mfgOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName()) && !mdseMfg.getName().equals(dto.getName())) {
                    verifyMFGName(dto.getName());
                    mdseMfg.setName(dto.getName());
                    update = true;
                }
                if (StringUtils.hasText(dto.getLogo()) && !mdseMfg.getLogo().equals(dto.getLogo())) {
                    mdseMfg.setLogo(dto.getLogo());
                    update = true;
                }
                if (StringUtils.hasText(dto.getAddress()) && !mdseMfg.getAddress().equals(dto.getAddress())) {
                    mdseMfg.setAddress(dto.getAddress());
                    update = true;
                }
                if (StringUtils.hasText(dto.getRemarks()) && !mdseMfg.getRemarks().equals(dto.getRemarks())) {
                    mdseMfg.setRemarks(dto.getRemarks());
                    update = true;
                }

                if (!ObjectUtils.isEmpty(dto.getShopId()) && !mdseMfg.getShopId().equals(dto.getShopId())){
                    verifyShopId(dto.getShopId());
                    mdseMfg.setShopId(dto.getShopId());
                    update = true;
                }

                if (update) {
                    return Optional.of(mfgRepository.save(mdseMfg));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseMfg> mdseMfgList = mfgRepository.findAllById(idList);
            mfgRepository.deleteAll(mdseMfgList);
        }
    }

    @Override
    public Optional<MdseMfg> findById(Long id) {
        return mfgRepository.findById(id);
    }

    @Override
    public MfgInfo entity2vo(MdseMfg entity) {
        if (entity != null) {
            MfgInfo info = new MfgInfo();
            info.setMfgId(entity.getMfgId());
            info.setName(entity.getName());
            info.setLogo(entity.getLogo());
            info.setAddress(entity.getAddress());
            info.setRemarks(entity.getRemarks());
            return info;
        }

        return null;
    }

    @Override
    public List<MfgInfo> list2vo(List<MdseMfg> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public List<MdseMfg> findAll() {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (Result.isSuccess(shopListResult)){
            List<ShopInfo> shopInfoList = shopListResult.getData();
            List<Long> shopIdList = shopInfoList.stream().map(ShopInfo::getShopId).collect(Collectors.toList());
            return mfgRepository.findAllByShopIdIn(shopIdList);
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
