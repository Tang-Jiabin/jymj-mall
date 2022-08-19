package com.jymj.mall.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增部门DTO
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */

@Data
@ApiModel(description = "添加商场")
public class AddMallDTO {

    @NotBlank(message = "商场名称不能为空")
    @ApiModelProperty("商场名称")
    private String name;

    @NotNull(message = "父级id不能为空")
    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("状态 1-正常")
    private Integer status;

    @ApiModelProperty("商场编号")
    private String mallNo;

    @ApiModelProperty("商场LOGO")
    private String logo;

    @ApiModelProperty("简介")
    private String introduce;

    @NotNull(message = "商场类型不能为空")
    @ApiModelProperty("商场类型 1-自营 2-授权等")
    private Integer type;

    @NotNull(message = "行政区id不能为空")
    @ApiModelProperty("行政区Id")
    private Long districtId;

    @NotNull(message = "标签id不能为空")
    @ApiModelProperty("标签id")
    private List<Long> tagId;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;

    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty("昵称")
    private String nickname;

}
