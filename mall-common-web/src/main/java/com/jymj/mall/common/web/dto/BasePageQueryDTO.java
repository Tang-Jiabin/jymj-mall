package com.jymj.mall.common.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
@Data
@ApiModel(description = "分页参数")
public class BasePageQueryDTO {

    @Min(1)
    @ApiModelProperty("页码")
    private Integer page;

    @Min(10)
    @Max(100)
    @ApiModelProperty("每页条数")
    private Integer size;

    @ApiModelProperty("排序方式 1-升序 2-降序")
    private Integer direction;

    @ApiModelProperty("排序字段 默认创建日期")
    private String properties;

    public Integer getPage() {
        if (this.page != null) {
            return this.page - 1;
        }
        return 0;
    }

    public Integer getSize() {
        if (this.size != null) {
            return this.size;
        }
        return 10;
    }
}
