package com.jymj.mall.shop.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalTime;

/**
 * 店铺装修
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@Entity
@Table(name = "shop_renovation")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update shop_renovation set deleted = 1 where renovation_id = ?")
@SQLDeleteAll(sql = "update shop_renovation set deleted = 1 where renovation_id in (?)")
public class ShopRenovation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_shop_renovation_id_seq")
    @SequenceGenerator(name = "mall_shop_renovation_id_seq",sequenceName = "mall_shop_renovation_id_seq",allocationSize = 1)
    private Long renovationId;

    @ApiModelProperty("父id")
    private Long parentId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("样式")
    private String style;

    @ApiModelProperty("数据")
    private String data;

    @ApiModelProperty("组件")
    private String components;

    @ApiModelProperty("状态")
    private Integer status;
}
