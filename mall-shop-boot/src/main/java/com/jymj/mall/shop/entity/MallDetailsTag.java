package com.jymj.mall.shop.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 商场标签关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Data
@Entity
@Table(name = "mall_details_tag")
@EqualsAndHashCode(callSuper=false)
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_details_tag SET deleted = 1 where mdt_id = ?")
@EntityListeners({AuditingEntityListener.class})
public class MallDetailsTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_details_tag_mdt_id_seq")
    @SequenceGenerator(name = "mall_details_tag_mdt_id_seq",sequenceName = "mall_details_tag_mdt_id_seq",allocationSize = 1)
    private Long mdtId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("标签Id")
    private Long tagId;

}
