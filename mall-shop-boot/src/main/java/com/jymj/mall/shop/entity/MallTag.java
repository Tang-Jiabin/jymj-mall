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
 * 商场标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Data
@Entity
@Table(name = "mall_tag")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_tag SET deleted = 1 where tag_id = ?")
@EqualsAndHashCode(callSuper=false)
@EntityListeners({AuditingEntityListener.class})
public class MallTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_tag_tag_id_seq")
    @SequenceGenerator(name = "mall_tag_tag_id_seq",sequenceName = "mall_tag_tag_id_seq",allocationSize = 1)
    private Long tagId;

    @ApiModelProperty("标签名称")
    private String name;


}
