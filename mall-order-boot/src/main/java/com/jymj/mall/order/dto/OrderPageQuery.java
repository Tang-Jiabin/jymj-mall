package com.jymj.mall.order.dto;

import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
@Data
@ApiModel("订单分页")
@EqualsAndHashCode(callSuper = true)
public class OrderPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("订单状态")
    private OrderStatusEnum orderStatus;
}
