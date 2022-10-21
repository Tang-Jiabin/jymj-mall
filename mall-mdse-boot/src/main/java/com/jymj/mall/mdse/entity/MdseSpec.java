package com.jymj.mall.mdse.entity;

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
 * 商品规格
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@Entity
@Table(name = "mdse_spec",indexes = {@Index(name = "mdse_spec_mdse_id",columnList = "mdseId")})
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_spec set deleted = 1 where spec_id = ?")
@SQLDeleteAll(sql = "update mdse_spec set deleted = 1 where spec_id in (?)")
public class MdseSpec extends BaseEntity {


    @Id
    @ApiModelProperty("规格id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mdse_spec_spec_id_seq")
    @SequenceGenerator(name = "mdse_spec_spec_id_seq",sequenceName = "mdse_spec_spec_id_seq",allocationSize = 1)
    private Long specId;

    @ApiModelProperty("规格名称")
    private String key;

    @ApiModelProperty("规格值")
    private String value;

    @ApiModelProperty("商品id")
    private Long mdseId;


}
