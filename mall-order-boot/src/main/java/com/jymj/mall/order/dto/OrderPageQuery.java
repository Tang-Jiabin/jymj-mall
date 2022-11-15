package com.jymj.mall.order.dto;

import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

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

    @ApiModelProperty(value = "店铺id")
    private List<Long> shopIdList;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("商品名称")
    private String mdseName;

    @ApiModelProperty("收货人")
    private String addressee;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("商品类型 1-商品  2-卡")
    private Integer type;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("订单下单开始时间")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("订单下单结束时间")
    private Date endDate;
}
