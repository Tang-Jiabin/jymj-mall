package com.jymj.mall.shop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 下拉列表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@ApiModel("Select选择器默认Option属性")
@Data
@NoArgsConstructor
public class OptionDTO<T> {

    public OptionDTO(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public OptionDTO(T value, String label, List<OptionDTO> children) {
        this.value = value;
        this.label = label;
        this.children= children;
    }

    @ApiModelProperty("选项的值")
    private T value;

    @ApiModelProperty("选项的标签")
    private String label;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<OptionDTO> children;


}
