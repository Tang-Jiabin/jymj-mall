package com.jymj.mall.shop.dto;

import lombok.Data;

import java.util.List;

/**
 * 装修状态
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-12-22
 */
@Data
public class RenovationStatusDTO {

    private List<Long> idList;

    private Integer status;

}
