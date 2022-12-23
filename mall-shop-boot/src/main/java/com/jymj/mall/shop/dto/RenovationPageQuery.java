package com.jymj.mall.shop.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 装修分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-12-22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RenovationPageQuery extends BasePageQueryDTO {

    private String title;
    private Integer status;
    private Integer homePage;
    private Long mallId;

}
