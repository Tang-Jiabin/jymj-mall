package com.jymj.mall.mdse.enums;


/**
 * 库存减少方式
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
public enum InventoryReductionMethod {

    /**
     * 库存减少方式
     */
    CREATE_ORDER(0,"下单减库存"),

    PAYMENT(1,"付款减库存");


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private final Integer value;

    private final String label;


    InventoryReductionMethod(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

}
