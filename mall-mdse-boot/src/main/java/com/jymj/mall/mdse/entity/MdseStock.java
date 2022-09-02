package com.jymj.mall.mdse.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import com.jymj.mall.mdse.dto.SpecDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@Entity
@Table(name = "mdse_stock")
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_stock set deleted = 1 where stock_id = ?")
@SQLDeleteAll(sql = "update mdse_stock set deleted = 1 where stock_id in (?)")
public class MdseStock extends BaseEntity {

    @Id
    @ApiModelProperty("库存id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mdse_stock_stock_id_seq")
    @SequenceGenerator(name = "mdse_stock_stock_id_seq",sequenceName = "mdse_stock_stock_id_seq",allocationSize = 1)
    private Long stockId;

    @ApiModelProperty("规格A")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spec_a")
    private MdseSpec specA;

    @ApiModelProperty("规格B")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spec_b")
    private MdseSpec specB;

    @ApiModelProperty("规格C")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spec_c")
    private MdseSpec specC;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("总库存")
    private Integer totalInventory;

    @ApiModelProperty("剩余库存")
    private Integer remainingStock;

    @ApiModelProperty("库存编号")
    private String number;

    @ApiModelProperty("规格图片")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id")
    private List<MallPicture> specPictureList;



}
