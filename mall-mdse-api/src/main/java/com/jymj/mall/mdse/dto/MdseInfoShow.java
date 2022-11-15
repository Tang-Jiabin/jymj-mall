package com.jymj.mall.mdse.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 商品显示信息
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MdseInfoShow {

    private Integer group;
    private Integer stock;
    private Integer label;
    private Integer mfg;
    private Integer type;
    private Integer brand;
    private Integer shop;
    private Integer picture;
}
