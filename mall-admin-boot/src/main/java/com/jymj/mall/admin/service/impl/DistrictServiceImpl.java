package com.jymj.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.admin.entity.SysDistrict;
import com.jymj.mall.admin.repository.SysDistrictRepository;
import com.jymj.mall.admin.service.DistrictService;
import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.web.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final SysDistrictRepository districtRepository;

    @Override
    public List<OptionVO> listDistrictOptions() {
        List<SysDistrict> districtList = districtRepository.findAllByDeleted(SystemConstants.DELETED_NO);
        return recursionTreeSelectList(SystemConstants.ROOT_DISTRICT_ID, districtList);
    }

    @Override
    public Optional<SysDistrict> findById(Long districtId) {

        return districtRepository.findByDistrictIdAndDeleted(districtId,SystemConstants.DELETED_NO);
    }

    @Override
    public List<SysDistrict> findAllByPid(Long districtId) {
        return districtRepository.findAllByPid(districtId);
    }

    @Override
    public List<SysDistrict> findChildren(Long districtId) {
        Optional<SysDistrict> districtOptional = findById(districtId);
        SysDistrict district = districtOptional.orElseThrow(() -> new BusinessException("行政区不存在"));
        return districtRepository.findAllByTreePathIsLikeAndDeleted(district.getTreePath(), SystemConstants.DELETED_NO);
    }

    @Override
    public List<DistrictInfo> list2vo(List<SysDistrict> districtList) {

        List<DistrictInfo> districtInfoList = Lists.newArrayList();
        Optional.ofNullable(districtList).orElse(Lists.newArrayList()).forEach(district -> {
            DistrictInfo districtInfo = district2vo(district);
            districtInfoList.add(districtInfo);
        });

        return districtInfoList;
    }

    @Override
    public List<SysDistrict> findParent(Long districtId) {
        Optional<SysDistrict> districtOptional = findById(districtId);
        SysDistrict district = districtOptional.orElseThrow(() -> new BusinessException("行政区不存在"));
        String[] districtIds = Optional.ofNullable(district.getTreePath()).orElse("").split(",");
        List<Long> districtIdList = Arrays.stream(districtIds).map(Long::parseLong).collect(Collectors.toList());
        return districtRepository.findAllById(districtIdList);
    }


    public DistrictInfo district2vo(SysDistrict district) {
        DistrictInfo districtInfo = new DistrictInfo();
        districtInfo.setDistrictId(district.getDistrictId());
        districtInfo.setPid(district.getPid());
        districtInfo.setName(district.getName());
        districtInfo.setCode(district.getCode());
        districtInfo.setCenter(district.getCenter());
        return districtInfo;
    }


    public static List<OptionVO> recursionTreeSelectList(long parentId, List<SysDistrict> districtList) {
        List<OptionVO> districtTreeSelectList = Lists.newArrayList();
        Optional.ofNullable(districtList).orElse(Lists.newArrayList())
                .stream()
                .filter(district -> district.getPid().equals(parentId))
                .forEach(district -> {
                    OptionVO option = new OptionVO(district.getDistrictId(), district.getName());
                    List<OptionVO> children = recursionTreeSelectList(district.getDistrictId(), districtList);
                    if (CollectionUtil.isNotEmpty(children)) {
                        option.setChildren(children);
                    }
                    districtTreeSelectList.add(option);
                });

        return districtTreeSelectList;
    }


}
