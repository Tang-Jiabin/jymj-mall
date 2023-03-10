package com.jymj.mall.order.vo;

import com.jymj.mall.mdse.vo.EffectiveRulesInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单商品详情
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-17
 */
@Data
public class MallOrderMdseDetailsInfo {

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("商品编号")
    private String number;

    @ApiModelProperty("库存id")
    private Long stockId;

    @ApiModelProperty("店铺id")
    private Long shopId;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @ApiModelProperty("商品类型 1-商品 2-卡")
    private Integer type;

    @ApiModelProperty("店铺名称")
    private String shopName;

    @ApiModelProperty("商品名称")
    private String mdseName;

    @ApiModelProperty("商品规格参数")
    private String mdseStockSpec;

    @ApiModelProperty("商品图片")
    private String mdsePicture;

    @ApiModelProperty("商品价格")
    private BigDecimal mdsePrice;

    @ApiModelProperty("使用状态 1-已使用 2-未使用")
    private Integer usageStatus;

    @ApiModelProperty("使用数量")
    private Integer usageQuantity;

    @ApiModelProperty("使用时间")
    private Date usageDate;

    @ApiModelProperty("订单卡商品")
    private List<MallOrderMdseDetailsInfo> cardMdseInfoList;

    @ApiModelProperty("卡生效规则")
    private EffectiveRulesInfo effectiveRules;
}
