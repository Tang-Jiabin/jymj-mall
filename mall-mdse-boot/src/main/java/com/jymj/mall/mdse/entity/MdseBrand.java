package com.jymj.mall.mdse.entity;

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
 * 品牌
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@Entity
@Table(name = "mdse_brand")
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_brand set deleted = 1 where brand_id = ?")
@SQLDeleteAll(sql = "update mdse_brand set deleted = 1 where brand_id in (?)")
public class MdseBrand extends BaseEntity {

    @Id
    @ApiModelProperty("品牌id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mdse_brand_brand_id_seq")
    @SequenceGenerator(name = "mdse_brand_brand_id_seq",sequenceName = "mdse_brand_brand_id_seq",allocationSize = 1)
    private Long brandId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("别名")
    private String alias;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("商场id")
    private Long mallId;
}
