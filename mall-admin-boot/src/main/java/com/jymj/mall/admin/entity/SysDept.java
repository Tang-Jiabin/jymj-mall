package com.jymj.mall.admin.entity;


import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import com.jymj.mall.common.web.pojo.BaseEntity;
/**
 * 部门表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
@Entity
@Table(name = "sys_dept")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysDept extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("树路径")
    private String treePath;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态")
    private Integer status;


}
