package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.entity.MdseBrand;
import com.jymj.mall.mdse.vo.BrandInfo;

import java.util.List;

/**
 * 品牌
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
public interface BrandService extends BaseService<MdseBrand, BrandInfo, BrandDTO> {


    List<MdseBrand> findAllByMallId(Long mallId);

    BrandInfo getBrandInfo(MdseBrand entity);
}
