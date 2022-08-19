package com.jymj.mall.admin.service;

import com.jymj.mall.admin.entity.SysDistrict;
import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.common.web.vo.OptionVO;

import java.util.List;
import java.util.Optional;

/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
public interface DistrictService {
    List<OptionVO> listDistrictOptions();

    Optional<SysDistrict> findById(Long districtId);

    List<SysDistrict> findAllByPid(Long districtId);

    List<SysDistrict> findChildren(Long districtId);

    List<DistrictInfo> list2vo(List<SysDistrict> districtList);

    List<SysDistrict> findParent(Long districtId);
}
