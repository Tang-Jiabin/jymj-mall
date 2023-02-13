package com.jymj.mall.order.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.order.dto.OrderDTO;
import com.jymj.mall.order.dto.OrderPageQuery;
import com.jymj.mall.order.dto.OrderPaySuccess;
import com.jymj.mall.order.entity.MallOrder;
import com.jymj.mall.order.vo.MallOrderInfo;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

/**
 * 订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
public interface OrderService extends BaseService<MallOrder, MallOrderInfo,OrderDTO> {
    Page<MallOrder> findPage(OrderPageQuery orderPageQuery);

    Optional<MallOrder> findByOrderNo(String orderNo);

    void paySuccess(OrderPaySuccess orderPaySuccess);

    void verify(VerifyOrderMdse verifyOrderMdse);

    Map<String, Integer> findOrderNumberByUserId(Long userId);
}
