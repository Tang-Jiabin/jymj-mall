package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.dto.MfgDTO;
import com.jymj.mall.mdse.entity.MdseBrand;
import com.jymj.mall.mdse.entity.MdseMfg;
import com.jymj.mall.mdse.vo.BrandInfo;
import com.jymj.mall.mdse.vo.MfgInfo;

import java.util.List;

/**
 * 厂家
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
public interface MfgService extends BaseService<MdseMfg, MfgInfo, MfgDTO> {

    List<MdseMfg> findAll();

    List<MdseMfg> findAllByMallId(Long mallId);
}
