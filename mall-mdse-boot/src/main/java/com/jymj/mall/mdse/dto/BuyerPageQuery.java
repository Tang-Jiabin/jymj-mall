package com.jymj.mall.mdse.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购买记录
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BuyerPageQuery extends BasePageQueryDTO {

    private Long userId;
    private Long mdseId;
    private Integer type;
}
