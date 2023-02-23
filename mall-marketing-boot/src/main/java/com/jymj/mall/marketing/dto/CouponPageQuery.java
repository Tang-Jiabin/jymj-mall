package com.jymj.mall.marketing.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 优惠券分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("商品分类id")
    private Long mdseCategoryId;

    @ApiModelProperty("用户id")
    private Long userId;


}
