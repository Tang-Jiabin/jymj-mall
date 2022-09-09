package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.dto.MfgDTO;
import com.jymj.mall.mdse.entity.MdseMfg;
import com.jymj.mall.mdse.repository.MdseMfgRepository;
import com.jymj.mall.mdse.service.MfgService;
import com.jymj.mall.mdse.vo.MfgInfo;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.MallInfo;
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

    private final MdseMfgRepository mdseMfgRepository;
    private final MallFeignClient mallFeignClient;
    private final DeptFeignClient deptFeignClient;

    @Override
    public MdseMfg add(MfgDTO dto) {


        MdseMfg mdseMfg = new MdseMfg();
        mdseMfg.setName(dto.getName());
        mdseMfg.setLogo(dto.getLogo());
        mdseMfg.setMallId(dto.getMallId());
        mdseMfg.setAddress(dto.getAddress());
        mdseMfg.setRemarks(dto.getRemarks());
        mdseMfg.setDeleted(SystemConstants.DELETED_NO);

        return mdseMfgRepository.save(mdseMfg);
    }



    @Override
    public Optional<MdseMfg> update(MfgDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getMfgId())) {
            Optional<MdseMfg> mfgOptional = mdseMfgRepository.findById(dto.getMfgId());
            if (mfgOptional.isPresent()) {
                MdseMfg mdseMfg = mfgOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName()) && !mdseMfg.getName().equals(dto.getName())) {

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

                if (!ObjectUtils.isEmpty(dto.getMallId()) && !mdseMfg.getMallId().equals(dto.getMallId())){

                    mdseMfg.setMallId(dto.getMallId());
                    update = true;
                }

                if (update) {
                    return Optional.of(mdseMfgRepository.save(mdseMfg));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseMfg> mdseMfgList = mdseMfgRepository.findAllById(idList);
            mdseMfgRepository.deleteAll(mdseMfgList);
        }
    }

    @Override
    public Optional<MdseMfg> findById(Long id) {
        return mdseMfgRepository.findById(id);
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
        Long deptId = UserUtils.getDeptId();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.tree(deptId);

        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            Result<List<MallInfo>> mallInfoListResult = mallFeignClient.getMallByDeptIdIn(StringUtils.collectionToCommaDelimitedString(deptIdList));
            if (Result.isSuccess(mallInfoListResult)){
                List<MallInfo> mallInfoList = mallInfoListResult.getData();
                List<Long> mallIdList = mallInfoList.stream().map(MallInfo::getMallId).collect(Collectors.toList());
                return mdseMfgRepository.findAllByMallIdIn(mallIdList);
            }
        }
        return Lists.newArrayList();
    }

    @Override
    public List<MdseMfg> findAllByMallId(Long mallId) {

        if (mallId!=null){
            return mdseMfgRepository.findAllByMallId(mallId);
        }
        return mdseMfgRepository.findAll();
    }


}
