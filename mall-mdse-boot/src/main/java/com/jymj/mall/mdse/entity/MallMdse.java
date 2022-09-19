package com.jymj.mall.mdse.entity;

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
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@Entity
@Table(name = "mall_mdse")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_mdse set deleted = 1 where mdse_id = ?")
@SQLDeleteAll(sql = "update mall_mdse set deleted = 1 where mdse_id in (?)")
public class MallMdse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_mdse_mdse_id_seq")
    @SequenceGenerator(name = "mall_mdse_mdse_id_seq",sequenceName = "mall_mdse_mdse_id_seq",allocationSize = 1)
    private Long mdseId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编号")
    private String number;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("库存数量")
    private Integer inventoryQuantity;

    @ApiModelProperty("剩余数量")
    private Integer remainingQuantity;

    @ApiModelProperty("销售数量")
    private Integer salesVolume;

    @ApiModelProperty("起售数量")
    private Integer startingQuantity;

    @ApiModelProperty("剩余数量 t-显示 f-不显示")
    private boolean showRemainingQuantity;

    @ApiModelProperty("退款 t-支持退款 f-不支持退款")
    private boolean refund;

    @ApiModelProperty("库存减少方式")
    private InventoryReductionMethod inventoryReductionMethod;

    @ApiModelProperty("购买按钮名称")
    private String buttonName;

    @ApiModelProperty("商品详情")
    private String details;

    @ApiModelProperty("品牌id")
    private Long brandId;

    @ApiModelProperty("厂家id")
    private Long mfgId;

    @ApiModelProperty("类型id")
    private Long typeId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("商品状态 1-上架 2-下架")
    private Integer status;
}
