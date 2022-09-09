package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("分组")
public class GroupDTO {

    @ApiModelProperty("分组id")
    private Long groupId;

    @NotBlank(message = "编码不能为空")
    @ApiModelProperty("编码")
    private String number;

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("显示")
    private Boolean show;

    @ApiModelProperty("备注")
    private String remarks;

    @NotNull(message = "商场id不能为空")
    @ApiModelProperty("商场id")
    private Long mallId;
}
