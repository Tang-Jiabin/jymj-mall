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
@Table(name = "mdse_mfg")
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_mfg set deleted = 1 where mfg_id = ?")
@SQLDeleteAll(sql = "update mdse_mfg set deleted = 1 where mfg_id in (?)")
public class MdseMfg extends BaseEntity {

    @Id
    @ApiModelProperty("厂家id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mdse_mfg_mfg_id_seq")
    @SequenceGenerator(name = "mdse_mfg_mfg_id_seq",sequenceName = "mdse_mfg_mfg_id_seq",allocationSize = 1)
    private Long mfgId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("店铺（网点）id")
    private Long shopId;
}
