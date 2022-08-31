package com.jymj.mall.shop.vo;

import com.jymj.mall.admin.vo.DistrictInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 商场信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-26
 */
@Data
public class MallInfo {

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("商场编号")
    private String mallNo;

    @ApiModelProperty("商场名称")
    private String name;

    @ApiModelProperty("商场LOGO")
    private String logo;

    @ApiModelProperty("简介")
    private String introduce;

    @ApiModelProperty("商场类型 1-自营 2-授权等")
    private String type;

    @ApiModelProperty("行政区")
    private DistrictInfo districtInfo;

    @ApiModelProperty("标签列表")
    private List<TagInfo> tagList;

    @ApiModelProperty("管理人姓名")
    private String managerName;

    @ApiModelProperty("管理人电话")
    private String managerMobile;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;
}
