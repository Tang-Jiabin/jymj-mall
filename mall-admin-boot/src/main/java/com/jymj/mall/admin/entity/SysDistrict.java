package com.jymj.mall.admin.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import com.jymj.mall.common.web.pojo.BaseEntity;
/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Data
@Entity
@Table(name = "sys_district")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysDistrict extends BaseEntity{
    @Id
    @ApiModelProperty("主键id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long districtId;

    @ApiModelProperty("父id")
    private Long pid;

    @ApiModelProperty("行政区名称")
    private String name;

    @ApiModelProperty("行政区代码")
    private String code;

    @ApiModelProperty("中心点")
    private String center;

    @ApiModelProperty("树路径")
    private String treePath;

}
