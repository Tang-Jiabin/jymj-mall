package com.jymj.mall.admin.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import com.jymj.mall.common.web.pojo.BaseEntity;
/**
 * 角色权限关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */

@Data
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "sys_role_permission")
@EntityListeners({AuditingEntityListener.class})
public class SysRolePermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rpId;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("权限Id")
    private Long permId;
}
