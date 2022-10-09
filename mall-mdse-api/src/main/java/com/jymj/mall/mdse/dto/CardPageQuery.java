package com.jymj.mall.mdse.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CardPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("商场id")
    private Long mallId;
}
