package com.jymj.mall.mdse.dto;

import lombok.Data;

import java.util.List;

/**
 * 更改商品状态
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-22
 */
@Data
public class MdseStatusDTO {

    private List<Long> mdseIds;

    private Integer status;

    private Long groupId;
}
