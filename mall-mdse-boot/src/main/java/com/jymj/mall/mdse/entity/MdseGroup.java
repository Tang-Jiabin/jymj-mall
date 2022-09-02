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
import java.util.List;
import java.util.Set;

/**
 * 商品分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@Entity
@Table(name = "mdse_group")
@Where(clause = " deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mdse_group set deleted = 1 where group_id = ?")
@SQLDeleteAll(sql = "update mdse_group set deleted = 1 where group_id in (?)")
public class MdseGroup extends BaseEntity {

    @Id
    @ApiModelProperty("分组id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mdse_group_group_id_seq")
    @SequenceGenerator(name = "mdse_group_group_id_seq",sequenceName = "mdse_group_group_id_seq",allocationSize = 1)
    private Long groupId;

    @ApiModelProperty("编码")
    private String number;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("显示")
    private Boolean show;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("网点id")
    private Long shopId;

}
