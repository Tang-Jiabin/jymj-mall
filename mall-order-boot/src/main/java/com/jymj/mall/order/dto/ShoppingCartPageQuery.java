package com.jymj.mall.order.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车分页
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShoppingCartPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("用户id")
    private Long userId;
}
