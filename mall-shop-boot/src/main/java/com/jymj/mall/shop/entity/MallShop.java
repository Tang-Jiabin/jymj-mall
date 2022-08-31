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
 * 商场店铺（网点）
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@Entity
@Table(name = "mall_shop")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_shop set deleted = 1 where shop_id = ?")
@SQLDeleteAll(sql = "update mall_shop set deleted = 1 where shop_id in (?)")
public class MallShop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_shop_shop_id_seq")
    @SequenceGenerator(name = "mall_shop_shop_id_seq",sequenceName = "mall_shop_shop_id_seq",allocationSize = 1)
    private Long shopId;

    @ApiModelProperty("店铺名称")
    private String name;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("负责人")
    private String director;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("营业状态 1-营业中 2-休息")
    private Integer inBusiness;

    @ApiModelProperty("营业开始时间")
    private LocalTime businessStartTime;

    @ApiModelProperty("营业结束时间")
    private LocalTime businessEndTime;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("部门id")
    private Long deptId;

}
