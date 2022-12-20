package com.jymj.mall.shop.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.shop.dto.RenovationDTO;
import com.jymj.mall.shop.entity.ShopRenovation;
import com.jymj.mall.shop.vo.RenovationInfo;

import java.util.List;

public interface RenovationService extends BaseService<ShopRenovation, RenovationInfo, RenovationDTO> {
    List<ShopRenovation> findAllByMallId(Long mallId);
}
