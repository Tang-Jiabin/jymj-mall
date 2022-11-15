package com.jymj.mall.mdse.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import com.jymj.mall.mdse.enums.EffectiveRulesEnum;
import com.jymj.mall.mdse.enums.UsageRulesEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 卡使用规则
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Data
@Entity
@Table(name = "mall_card_rules")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_card_rules set deleted = 1 where rules_id = ?")
@SQLDeleteAll(sql = "update mall_card_rules set deleted = 1 where rules_id in (?)")
public class MdseCardRules extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_card_rules_id_seq")
    @SequenceGenerator(name = "mall_card_rules_id_seq",sequenceName = "mall_card_rules_id_seq",allocationSize = 1)
    private Long rulesId;

    @ApiModelProperty(value = "生效规则")
    private EffectiveRulesEnum effectiveRules;

    @ApiModelProperty(value = "几小时后生效")
    private Integer hoursLater;

    @ApiModelProperty("使用规则")
    private UsageRulesEnum usageRule;

    @ApiModelProperty("几天后内可用")
    private Integer days;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("可用开始时间")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("可用结束时间")
    private LocalDate endDate;

    @ApiModelProperty("商品id")
    private Long cardId;
}
