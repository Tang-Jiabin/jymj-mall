package com.jymj.mall.mdse.dto;

import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 商品dto
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("商品")
public class MdseDTO {

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("商品图片")
    private List<PictureDTO> pictureList;

    @ApiModelProperty("商品视频")
    private List<PictureDTO> videoList;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编号")
    private String number;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("运费")
    private BigDecimal postage;

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

    @ApiModelProperty("商品详情")
    private String details;

    @ApiModelProperty("分组id集合")
    private Set<Long> groupIdList;

    @ApiModelProperty("店铺（网点）id集合")
    private Set<Long> shopIdList;

    @ApiModelProperty("标签id集合")
    private Set<Long> labelIdList;

    @ApiModelProperty("品牌id")
    private Long brandId;

    @ApiModelProperty("厂家id")
    private Long mfgId;

    @ApiModelProperty("类型id")
    private Long typeId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("库存规格集合")
    private Set<StockDTO> stockList;

    @ApiModelProperty("商品状态 1-上架 2-下架")
    private Integer status;

}
