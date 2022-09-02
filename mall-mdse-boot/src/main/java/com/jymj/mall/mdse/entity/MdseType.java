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
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@Entity
@Table(name = "mdse_type")
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_type set deleted = 1 where type_id = ?")
@SQLDeleteAll(sql = "update mdse_type set deleted = 1 where type_id in (?)")
public class MdseType extends BaseEntity {

    @Id
    @ApiModelProperty("类型id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mdse_type_type_id_seq")
    @SequenceGenerator(name = "mdse_type_type_id_seq",sequenceName = "mdse_type_type_id_seq",allocationSize = 1)
    private Long typeId;

    @ApiModelProperty("类型名称")
    private String name;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("店铺(网点)id")
    private Long shopId;
}
