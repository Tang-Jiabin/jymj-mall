package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.TypeDTO;
import com.jymj.mall.mdse.entity.MdseType;
import com.jymj.mall.mdse.vo.TypeInfo;

import java.util.List;

/**
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
public interface TypeService extends BaseService<MdseType, TypeInfo, TypeDTO> {
    List<MdseType> findAllByAuth();

    List<MdseType> findAllByMallId(Long mallId);
}
