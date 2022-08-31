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
 * 权限
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
@Entity
@Table(name = "sys_permission")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_permission SET deleted = 1 where perm_id = ?")
@SQLDeleteAll(sql = "UPDATE sys_permission SET deleted = 1 where perm_id in (?)")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysPermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sys_permission_perm_id_seq")
    @SequenceGenerator(name = "sys_permission_perm_id_seq",sequenceName = "sys_permission_perm_id_seq",allocationSize = 1)
    private Long permId;

    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @ApiModelProperty("资源路径")
    private String urlPerm;

    @ApiModelProperty("按钮路径")
    private String btnPerm;


}
