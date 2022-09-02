package com.jymj.mall.mdse.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分组分页查询
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("编码")
    private String number;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("显示")
    private Boolean show;
}
