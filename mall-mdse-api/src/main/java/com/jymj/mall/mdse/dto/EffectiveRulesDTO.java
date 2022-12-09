package com.jymj.mall.mdse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jymj.mall.mdse.enums.EffectiveRulesEnum;
import com.jymj.mall.mdse.enums.UsageRulesEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 生效规则
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Data
@ApiModel(value = "生效规则")
public class EffectiveRulesDTO {

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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("可用结束时间")
    private Date endDate;

}
