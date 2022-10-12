package com.jymj.mall.order.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.order.dto.ShoppingCartMdseDTO;
import com.jymj.mall.order.dto.ShoppingCartPageQuery;
import com.jymj.mall.order.entity.ShoppingCartMdse;
import com.jymj.mall.order.vo.ShoppingCartMdseInfo;
import org.springframework.data.domain.Page;

/**
 * 购物车
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
public interface ShoppingCartService extends BaseService<ShoppingCartMdse, ShoppingCartMdseInfo, ShoppingCartMdseDTO> {
    Page<ShoppingCartMdse> findPage(ShoppingCartPageQuery shoppingCartPageQuery);
}
