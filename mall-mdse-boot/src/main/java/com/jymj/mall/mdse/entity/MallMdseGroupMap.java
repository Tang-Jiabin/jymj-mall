package com.jymj.mall.mdse.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 商品分组中间表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */

@Data
@Entity
@Table(name = "mall_mdse_group_map")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_mdse_group_map set deleted = 1 where mg_id = ?")
@SQLDeleteAll(sql = "update mall_mdse_group_map set deleted = 1 where mg_id in (?)")
public class MallMdseGroupMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_mdse_group_mg_id_seq")
    @SequenceGenerator(name = "mall_mdse_group_mg_id_seq",sequenceName = "mall_mdse_group_mg_id_seq",allocationSize = 1)
    private Long mgId;

    private Long mdseId;

    private Long groupId;

}
