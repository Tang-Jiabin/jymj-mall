package com.jymj.mall.mdse.vo;


import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 卡
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-07
 */
@Data
@ApiModel(value = "卡")
public class CardInfo {

    @ApiModelProperty("卡id")
    private Long cardId;

    @ApiModelProperty("卡图片")
    private List<PictureInfo> pictureList;

    @ApiModelProperty("卡名称")
    private String name;

    @ApiModelProperty("卡价格")
    private BigDecimal price;

    @ApiModelProperty("库存数量")
    private Integer inventoryQuantity;

    @ApiModelProperty("起售数量")
    private Integer startingQuantity;

    @ApiModelProperty("剩余数量 t-显示 f-不显示")
    private Boolean showRemainingQuantity;

    @ApiModelProperty("退款 t-支持退款 f-不支持退款")
    private Boolean refund;

    @ApiModelProperty("库存减少方式")
    private InventoryReductionMethod inventoryReductionMethod;

    @ApiModelProperty("购买按钮名称")
    private String buttonName;

    @ApiModelProperty("卡详情")
    private String details;

    @ApiModelProperty("类型id")
    private Long typeId;

    @ApiModelProperty("库存规格Id集合")
    private Set<Long> stockIdList;

    @ApiModelProperty("卡状态 1-上架 2-下架")
    private Integer status;

    @ApiModelProperty("生效规则")
    private EffectiveRulesInfo effectiveRules;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("商品列表")
    private List<MdseInfo> mdseInfoList;
}
