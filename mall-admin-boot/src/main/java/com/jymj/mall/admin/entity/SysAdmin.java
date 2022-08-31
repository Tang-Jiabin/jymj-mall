package com.jymj.mall.admin.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
/**
 * 系统管理员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
@Entity
@Table(name = "sys_admin")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_admin SET deleted = 1 where admin_id = ?")
@SQLDeleteAll(sql = "UPDATE sys_admin SET deleted = 1 where admin_id in (?)")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysAdmin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sys_admin_admin_id_seq")
    @SequenceGenerator(name = "sys_admin_admin_id_seq",sequenceName = "sys_admin_admin_id_seq",allocationSize = 1)
    private Long adminId;

    @ApiModelProperty("编号")
    private String number;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("性别 1-男 2-女")
    private Integer gender;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("部门ID")
    private Long deptId;


}
