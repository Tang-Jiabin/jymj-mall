package com.jymj.mall.mdse.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 卡商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_card_mdse")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_card_mdse set deleted = 1 where cm_id = ?")
@SQLDeleteAll(sql = "update mall_card_mdse set deleted = 1 where cm_id in (?)")
public class CardMdse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_card_mdse_cm_id_seq")
    @SequenceGenerator(name = "mall_card_mdse_cm_id_seq",sequenceName = "mall_card_mdse_cm_id_seq",allocationSize = 1)
    private Long cmId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("库存id")
    private Long stockId;

    @ApiModelProperty("卡id")
    private Long cardId;

    @ApiModelProperty("数量")
    private Integer quantity;
}
