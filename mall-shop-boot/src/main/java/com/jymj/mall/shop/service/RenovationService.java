package com.jymj.mall.shop.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.shop.dto.RenovationDTO;
import com.jymj.mall.shop.dto.RenovationPageQuery;
import com.jymj.mall.shop.dto.RenovationStatusDTO;
import com.jymj.mall.shop.entity.ShopRenovation;
import com.jymj.mall.shop.vo.RenovationInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RenovationService extends BaseService<ShopRenovation, RenovationInfo, RenovationDTO> {

    List<ShopRenovation> findAllByMallIdAndStatus(Long mallId,Integer status);

    void updateStatus(RenovationStatusDTO renovationStatusDTO);

    void updateHome(RenovationDTO renovationDTO);

    List<ShopRenovation> findAllByMallId(Long mallId);

    Page<ShopRenovation> findPage(RenovationPageQuery renovationPageQuery);

}
