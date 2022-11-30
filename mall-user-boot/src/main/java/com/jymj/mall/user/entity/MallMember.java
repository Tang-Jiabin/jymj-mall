package com.jymj.mall.user.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 会员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-07
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mall_member")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_member set deleted = 1 where member_id = ?")
@SQLDeleteAll(sql = "update mall_member set deleted = 1 where member_id in (?)")
public class MallMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_member_member_id_seq")
    @SequenceGenerator(name = "mall_member_member_id_seq", sequenceName = "mall_member_member_id_seq", allocationSize = 1)
    private Long memberId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty(value = "会员等级")
    private Integer level;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("身份证号")
    private String idNumber;

    @NotNull(message = "邮箱不能为空")
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "状态")
    private Integer state;
}
