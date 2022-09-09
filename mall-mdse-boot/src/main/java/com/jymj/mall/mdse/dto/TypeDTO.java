package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

/**
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@ApiModel("商品类型")
public class TypeDTO {

    @ApiModelProperty("类型id")
    private Long typeId;

    @NotNull(message = "名称不能为空")
    @ApiModelProperty("类型名称")
    private String name;

    @ApiModelProperty("备注")
    private String remarks;

    @NotNull(message = "商场id不能为空")
    @ApiModelProperty("商场id")
    private Long mallId;
}
