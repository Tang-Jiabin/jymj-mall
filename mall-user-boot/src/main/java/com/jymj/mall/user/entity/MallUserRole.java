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
 * 用户角色关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-24
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mall_user_role")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_user_role set deleted = 1 where ur_id = ?")
@SQLDeleteAll(sql = "update mall_user_role set deleted = 1 where ur_id in (?)")
public class MallUserRole extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_user_role_id_seq")
    @SequenceGenerator(name = "mall_user_role_id_seq", sequenceName = "mall_user_role_id_seq", allocationSize = 1)
    private Long urId;

    @ApiModelProperty("角色id")
    private Long roleId;

    @ApiModelProperty("用户id")
    private Long userId;
}
