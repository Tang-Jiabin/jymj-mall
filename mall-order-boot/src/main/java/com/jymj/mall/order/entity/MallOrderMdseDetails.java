package com.jymj.mall.order.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 订单商品详情
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
@Data
@Entity
@Table(name = "mall_order_mdse_details",indexes = {@Index(name = "mall_order_mdse_details_order_id",columnList = "orderId")})
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_order_mdse_details SET deleted = 1 where order_mdse_details_id = ?")
@SQLDeleteAll(sql = "UPDATE mall_order_mdse_details SET deleted = 1 where order_mdse_details_id in (?)")
@EqualsAndHashCode(callSuper=true)
@EntityListeners({AuditingEntityListener.class})
public class MallOrderMdseDetails extends BaseEntity {

    @Id
    @ApiModelProperty("订单商品详情id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_order_mdse_details_id_seq")
    @SequenceGenerator(name = "mall_order_mdse_details_id_seq",sequenceName = "mall_order_mdse_details_id_seq",allocationSize = 1)
    private Long orderMdseDetailsId;

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("库存id")
    private Long stockId;

    @ApiModelProperty("店铺id")
    private Long shopId;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @ApiModelProperty("商品类型 1-商品 2-卡")
    private Integer type;

    @ApiModelProperty("店铺名称")
    private String shopName;

    @ApiModelProperty("商品名称")
    private String mdseName;

    @ApiModelProperty("商品规格参数")
    private String mdseStockSpec;

    @ApiModelProperty("商品图片")
    private String mdsePicture;

    @ApiModelProperty("商品价格")
    private BigDecimal mdsePrice;

    @ApiModelProperty("库存减少方式")
    private InventoryReductionMethod inventoryReductionMethod;

}
