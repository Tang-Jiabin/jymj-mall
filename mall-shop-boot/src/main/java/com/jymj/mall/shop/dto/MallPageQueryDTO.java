package com.jymj.mall.shop.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商场分页查询
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(description = "商场查询分页")
public class MallPageQueryDTO extends BasePageQueryDTO {


    @ApiModelProperty("商场名称")
    private String name;

    @ApiModelProperty("商场类型 1-自营 2-授权等")
    private Integer type;

    @ApiModelProperty("行政区Id")
    private Long districtId;

    @ApiModelProperty("管理人姓名")
    private String managerName;

    @ApiModelProperty("管理人电话")
    private String managerMobile;
}
