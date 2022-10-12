package com.jymj.mall.order.entity;

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
 * 购物车商品
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Data
@Entity
@Table(name = "mall_shopping_cart", indexes = {@Index(name = "user_id_index", columnList = "userId")})
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_shopping_cart set deleted = 1 where shopping_cart_id = ?")
@SQLDeleteAll(sql = "update mall_shopping_cart set deleted = 1 where shopping_cart_id in (?)")
public class ShoppingCartMdse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_shopping_cart_id_id_seq")
    @SequenceGenerator(name = "mall_shopping_cart_id_id_seq", sequenceName = "mall_shopping_cart_id_id_seq", allocationSize = 1)
    private Long shoppingCartId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("库存id")
    private Long stockId;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("店铺id")
    private Long shopId;

}
