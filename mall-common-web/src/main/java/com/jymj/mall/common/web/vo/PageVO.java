package com.jymj.mall.common.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
@Data
@ApiModel("分页")
public class PageVO<T> {

    @ApiModelProperty("内容")
    private List<T> content;

    @ApiModelProperty("总页数")
    private Integer totalPages;

    @ApiModelProperty("总条数")
    private Long totalElements;

    @ApiModelProperty("第几页")
    private Integer number;

    @ApiModelProperty("每页多少条")
    private Integer size;

    @ApiModelProperty("是否是第一页")
    private Boolean first;

    @ApiModelProperty("是否是最后一页")
    private Boolean last;

    @ApiModelProperty("在总数里有多少条")
    private Integer numberOfElements;

    @ApiModelProperty("是否是空")
    private Boolean empty;

    public Integer getNumber() {
        return this.number + 1;
    }
}
