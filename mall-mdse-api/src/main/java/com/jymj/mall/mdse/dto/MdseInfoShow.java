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

    private boolean group;
    private boolean stock;
    private boolean label;
    private boolean mfg;
    private boolean type;
    private boolean brand;
    private boolean shop;
    private boolean picture;
}
