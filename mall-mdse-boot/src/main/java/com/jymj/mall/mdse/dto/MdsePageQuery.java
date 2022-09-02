package com.jymj.mall.mdse.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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

    @ApiModelProperty("商品起始创建时间")
    private Date startCreateDate;

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

    @ApiModelProperty("标签id")
    private Long labelId;

    @ApiModelProperty("商品起始价格")
    private BigDecimal startPrice;

    @ApiModelProperty("商品结束价格")
    private BigDecimal endPrice;

}
