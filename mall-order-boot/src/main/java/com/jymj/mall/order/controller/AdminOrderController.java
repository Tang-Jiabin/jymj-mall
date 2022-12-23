package com.jymj.mall.order.controller;

import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.order.dto.OrderDTO;
import com.jymj.mall.order.dto.OrderPageQuery;
import com.jymj.mall.order.entity.MallOrder;
import com.jymj.mall.order.service.OrderService;
import com.jymj.mall.order.vo.MallOrderInfo;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import com.jymj.mall.shop.vo.ShopInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 管理员订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-12
 */
@Api(tags = "管理员订单")
@RestController
@RequestMapping("/api/v1/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final ShopFeignClient shopFeignClient;




    @ApiOperation(value = "订单信息")
    @GetMapping("/no/{orderNo}/info")
    public Result<MallOrderInfo> getOrderByNo(@PathVariable String orderNo) {
        Optional<MallOrder> orderOptional = orderService.findByOrderNo(orderNo);
        MallOrder mallOrder = orderOptional.orElseThrow(() -> new BusinessException("订单不存在"));
        MallOrderInfo orderInfo = orderService.entity2vo(mallOrder);
        return Result.success(orderInfo);
    }

    @ApiOperation(value = "订单信息")
    @GetMapping("/id/{orderId}/info")
    @Cacheable(cacheNames = "mall-order:order-info:", key = "'order-id:'+#orderId")
    public Result<MallOrderInfo> getOrderById(@PathVariable Long orderId) {
        Optional<MallOrder> orderOptional = orderService.findById(orderId);
        MallOrder mallOrder = orderOptional.orElseThrow(() -> new BusinessException("订单不存在"));
        MallOrderInfo orderInfo = orderService.entity2vo(mallOrder);
        return Result.success(orderInfo);
    }

    @ApiOperation(value = "更新订单")
    @PutMapping
    public Result<Object> update( @RequestBody OrderDTO orderDTO) {
        if (orderDTO.getStatusEnum() != OrderStatusEnum.CANCELED && orderDTO.getStatusEnum() != OrderStatusEnum.COMPLETED && orderDTO.getStatusEnum() != OrderStatusEnum.CLOSED) {
            return Result.failed("订单状态错误");
        }
        orderService.update(orderDTO);
        return Result.success();
    }

    @ApiOperation(value = "删除订单")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteDept(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        orderService.delete(ids);
        return Result.success();
    }


    @ApiOperation(value = "订单分页")
    @GetMapping("/pages")
    public Result<PageVO<MallOrderInfo>> pages(OrderPageQuery orderPageQuery) {

        Result<List<ShopInfo>> shopListResult = Objects.nonNull(orderPageQuery.getMallId()) ? shopFeignClient.getShopByMallId(orderPageQuery.getMallId()) : shopFeignClient.lists();

        if (!Result.isSuccess(shopListResult)) {
            throw new BusinessException("授权信息错误");
        }

        List<Long> shopIdList = shopListResult.getData().stream().map(ShopInfo::getShopId).collect(Collectors.toList());
        List<Long> ids = ObjectUtils.isEmpty(orderPageQuery.getShopIdList()) ? shopIdList : orderPageQuery.getShopIdList().stream().filter(shopIdList::contains).collect(Collectors.toList());
        orderPageQuery.setShopIdList(ids);

        Page<MallOrder> page = orderService.findPage(orderPageQuery);
        List<MallOrderInfo> shoppingCartMdseInfoList = orderService.list2vo(page.getContent());
        PageVO<MallOrderInfo> pageVo = PageUtils.toPageVO(page, shoppingCartMdseInfoList);
        return Result.success(pageVo);
    }


    @ApiIgnore
    @ApiOperation(value = "订单核销")
    @PutMapping("/verify")
    public Result<Object> verify(@RequestBody VerifyOrderMdse verifyOrderMdse) {
        orderService.verify(verifyOrderMdse);
        return Result.success();
    }

}
