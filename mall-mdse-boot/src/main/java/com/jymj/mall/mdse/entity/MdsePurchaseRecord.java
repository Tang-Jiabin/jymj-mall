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
 * 购买记录
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-09
 */
@Data
@Entity
@Table(name = "mdse_purchase_record")
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_purchase_record set deleted = 1 where id = ?")
@SQLDeleteAll(sql = "update mdse_purchase_record set deleted = 1 where id in (?)")
public class MdsePurchaseRecord extends BaseEntity {


    @Id
    @ApiModelProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mdse_purchase_record_id_seq")
    @SequenceGenerator(name = "mdse_purchase_record_id_seq", sequenceName = "mdse_purchase_record_id_seq", allocationSize = 1)
    private Long id;

    private Long orderId;

    private Long mdseId;

    private Long userId;

    private Integer type;
}
