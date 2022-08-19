package com.jymj.mall.admin.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import com.jymj.mall.common.web.pojo.BaseEntity;
/**
 * 管理员角色关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
@Entity
@Table(name = "sys_admin_role")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysAdminRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long arId;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("角色id")
    private Long roleId;



}
