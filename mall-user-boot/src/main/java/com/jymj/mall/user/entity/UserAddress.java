package com.jymj.mall.user.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 用户地址
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_user_address", indexes = {@Index(name = "mall_user_address_user_id", columnList = "userId")})
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_user_address SET deleted = 1 where address_id = ?")
@SQLDeleteAll(sql = "UPDATE mall_user_address SET deleted = 1 where address_id in (?)")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_user_address_id_seq")
    @SequenceGenerator(name = "mall_user_address_id_seq", sequenceName = "mall_user_address_id_seq", allocationSize = 1)
    private Long addressId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("行政区")
    private String region;

    @ApiModelProperty("详细地址")
    private String detailedAddress;

    @ApiModelProperty("标签")
    private String label;

    @ApiModelProperty("状态 1-默认")
    private Integer status;


}
