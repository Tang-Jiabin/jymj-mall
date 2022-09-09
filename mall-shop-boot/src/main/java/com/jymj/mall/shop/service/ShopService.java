package com.jymj.mall.shop.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.shop.dto.ShopDTO;
import com.jymj.mall.shop.dto.ShopPageQuery;
import com.jymj.mall.shop.entity.MallShop;
import com.jymj.mall.shop.vo.ShopInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 店铺（网点）
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
public interface ShopService extends BaseService<MallShop, ShopInfo, ShopDTO> {
    Page<MallShop> findPage(ShopPageQuery shopPageQuery);

    List<MallShop> findAllByDeptId(Long deptId);

    List<MallShop> findAllById(String ids);
}
