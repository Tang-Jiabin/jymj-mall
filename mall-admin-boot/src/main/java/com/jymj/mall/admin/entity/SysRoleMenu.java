package com.jymj.mall.admin.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
/**
 * 角色菜单关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */

@Data
@Entity
@Table(name = "sys_role_menu")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_role_menu SET deleted = 1 where rm_id = ?")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysRoleMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sys_role_menu_rm_id_seq")
    @SequenceGenerator(name = "sys_role_menu_rm_id_seq",sequenceName = "sys_role_menu_rm_id_seq",allocationSize = 1)
    private Long rmId;

    @ApiModelProperty("角色id")
    private Long roleId;

    @ApiModelProperty("菜单ID")
    private Long menuId;



}
