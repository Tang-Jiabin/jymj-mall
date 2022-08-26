package com.jymj.mall.admin.entity;


import com.jymj.mall.common.enums.MenuTypeEnum;
import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import com.jymj.mall.common.web.pojo.BaseEntity;
/**
 * 菜单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
@Entity
@Table(name = "sys_menu")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_menu SET deleted = 1 where menu_id = ?")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sys_menu_menu_id_seq")
    @SequenceGenerator(name = "sys_menu_menu_id_seq",sequenceName = "sys_menu_menu_id_seq",allocationSize = 1)
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
