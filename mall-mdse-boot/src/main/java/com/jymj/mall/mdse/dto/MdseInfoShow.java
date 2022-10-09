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

    private boolean group = false;
    private boolean stock = false;
    private boolean label = false;
    private boolean mfg = false;
    private boolean type = false;
    private boolean brand = false;
    private boolean shop = false;
    private boolean picture = false;
}
