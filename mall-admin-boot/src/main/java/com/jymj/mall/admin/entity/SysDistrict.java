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
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE sys_district SET deleted = 1 where district_id = ?")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class SysDistrict extends BaseEntity{
    @Id
    @ApiModelProperty("主键id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sys_district_district_id_seq")
    @SequenceGenerator(name = "sys_district_district_id_seq",sequenceName = "sys_district_district_id_seq",allocationSize = 1)
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
