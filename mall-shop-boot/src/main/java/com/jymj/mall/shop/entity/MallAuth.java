package com.jymj.mall.shop.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


/**
 * 商场认证
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Data
@Entity

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_auth")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update  mall_auth set deleted = 1 where auth_id = ?")
public class MallAuth extends BaseEntity {

    @Id
    @SequenceGenerator(
            name = "mall_auth_auth_id_seq",
            sequenceName = "mall_auth_auth_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "mall_auth_auth_id_seq"
    )
    @ApiModelProperty("授权id")
    private Long authId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("公司地址")
    private String companyAddress;

    @ApiModelProperty("法人")
    private String legalPerson;

    @ApiModelProperty("身份证")
    private String  identity;

    @ApiModelProperty("统一社会信用代码")
    private String unifiedSocialCreditCode;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("营业执照")
    private String license;

    @ApiModelProperty("身份证（正面）")
    private String idFront;

    @ApiModelProperty("身份证（反面）")
    private String idBack;


}
