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
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_dept SET deleted = 1 where dept_id = ?")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysDept extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="sys_dept_dept_id_seq")
    @SequenceGenerator(name = "sys_dept_dept_id_seq",sequenceName = "sys_dept_dept_id_seq",allocationSize = 1)
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
