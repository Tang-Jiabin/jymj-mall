package com.jymj.mall.mdse.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品分页查询
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MdsePageQuery extends BasePageQueryDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("商品起始创建时间")
    private Date startCreateDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("商品结束创建时间")
    private Date endCreateDate;

    @ApiModelProperty("商品编号")
    private String number;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("分组id")
    private Long groupId;

    @ApiModelProperty("库存减少方式")
    private InventoryReductionMethod inventoryReductionMethod;

    @ApiModelProperty("类型id")
    private Long typeId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("商店id")
    private Long shopId;

    @ApiModelProperty("品牌id")
    private Long brandId;

    @ApiModelProperty("标签id")
    private Long labelId;

    @ApiModelProperty("商品起始价格")
    private BigDecimal startPrice;

    @ApiModelProperty("商品结束价格")
    private BigDecimal endPrice;

    @ApiModelProperty("商品库存大于等于")
    private Integer quantityGreaterThanOrEqual;

    @ApiModelProperty("商品库存小于等于")
    private Integer quantityLessThanOrEqual;

    @ApiModelProperty("商品销量大于等于")
    private Integer salesVolumeGreaterThanOrEqual;

    @ApiModelProperty("商品销量小于等于")
    private Integer salesVolumeLessThanOrEqual;

    @ApiModelProperty("商品状态 1-上架 2-下架")
    private Integer status;

}
