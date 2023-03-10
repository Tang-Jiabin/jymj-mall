package com.jymj.mall.admin.dto;

import com.jymj.mall.common.enums.MenuTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-12-07
 */
@Data
public class MenuDTO {

    @ApiModelProperty("菜单Id")
    private Long menuId;

    @ApiModelProperty("父级Id")
    private Long parentId;

    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("路由相对路径")
    private String path;

    @ApiModelProperty("组件绝对路径")
    private String component;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("显示")
    private Integer visible;

    @ApiModelProperty("重定向")
    private String redirect;

    @ApiModelProperty("菜单类型(1:菜单；2：目录；3：外链)")
    private MenuTypeEnum type;

}
