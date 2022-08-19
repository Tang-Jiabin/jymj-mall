package com.jymj.mall.admin.entity;


import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import com.jymj.mall.common.web.pojo.BaseEntity;
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
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysPermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
