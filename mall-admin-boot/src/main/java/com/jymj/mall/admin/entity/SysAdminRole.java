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
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_admin_role SET deleted = 1 where ar_id = ?")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysAdminRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sys_admin_role_ar_id_seq")
    @SequenceGenerator(name = "sys_admin_role_ar_id_seq",sequenceName = "sys_admin_role_ar_id_seq",allocationSize = 1)
    private Long arId;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("角色id")
    private Long roleId;



}
