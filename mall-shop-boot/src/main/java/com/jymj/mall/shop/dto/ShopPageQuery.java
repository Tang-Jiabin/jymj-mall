package com.jymj.mall.shop.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺（网点）分页查询
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("店铺名称")
    private String name;

    @ApiModelProperty("负责人")
    private String director;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("状态")
    private Integer status;
}
