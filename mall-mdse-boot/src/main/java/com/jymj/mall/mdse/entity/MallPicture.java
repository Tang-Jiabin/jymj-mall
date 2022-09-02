package com.jymj.mall.mdse.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import com.jymj.mall.mdse.enums.PictureType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 商品图片
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@Entity
@Table(name = "mall_picture")
@Where(clause = "deleted = 0")
@EntityListeners({AuditingEntityListener.class})
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "update mall_picture set deleted = 1 where picture_id = ?")
@SQLDeleteAll(sql = "update mall_picture set deleted = 1 where picture_id in (?)")
public class MallPicture extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_picture_picture_id_seq")
    @SequenceGenerator(name = "mall_picture_picture_id_seq",sequenceName = "mall_picture_picture_id_seq",allocationSize = 1)
    private Long pictureId;

    @ApiModelProperty("图片地址")
    private String url;

    @ApiModelProperty("图片类型")
    private PictureType type;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("规格id")
    private Long stockId;
}
