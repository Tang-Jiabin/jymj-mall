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
 * 核销人员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-27
 */
@Data
@Entity
@Table(name = "mall_verify_person")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_verify_person SET deleted = 1 where id = ?")
@EqualsAndHashCode(callSuper=true)
@EntityListeners({AuditingEntityListener.class})
public class VerifyPerson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_verify_person_id_seq")
    @SequenceGenerator(name = "mall_verify_person_id_seq",sequenceName = "mall_verify_person_id_seq",allocationSize = 1)
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("管理员名")
    private String adminName;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("店铺id（1,2,3,4）英文逗号分割")
    private String shopIds;

    @ApiModelProperty("商品id（1,2,3,4）英文逗号分割")
    private String mdseIds;
}
