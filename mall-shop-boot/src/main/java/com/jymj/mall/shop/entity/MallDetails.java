package com.jymj.mall.shop.entity;


import com.jymj.mall.common.web.pojo.BaseEntity;
import com.jymj.mall.shop.enums.MallType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 商场详情
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */

@Data
@Entity
@Table(name = "mall_details")
@EqualsAndHashCode(callSuper=false)
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_details SET deleted = 1 where mall_id = ?")
@EntityListeners({AuditingEntityListener.class})
public class MallDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_details_mall_id_seq")
    @SequenceGenerator(name = "mall_details_mall_id_seq",sequenceName = "mall_details_mall_id_seq",allocationSize = 1)
    private Long mallId;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("商场编号")
    private String mallNo;

    @ApiModelProperty("商场名称")
    private String name;

    @ApiModelProperty("商场LOGO")
    private String logo;

    @ApiModelProperty("简介")
    private String introduce;

    @ApiModelProperty("商场类型")
    private MallType type;

    @ApiModelProperty("行政区Id")
    private Long districtId;

    @ApiModelProperty("管理人姓名")
    private String managerName;

    @ApiModelProperty("管理人电话")
    private String managerMobile;

}
