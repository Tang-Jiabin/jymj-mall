package com.jymj.mall.pay.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.pay.service.MallPayService;
import com.jymj.mall.pay.vo.StatisticsInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 记录
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-30
 */
@Api(tags = "消费统计")
@Slf4j
@RestController
@RequestMapping("/api/v1/statistics/")
@RequiredArgsConstructor
public class StatisticsController {

    private final MallPayService mallPayService;

    @ApiOperation(value = "用户消费统计")
    @GetMapping("/user/{userId}")
    public Result<StatisticsInfo> getByUserId(@PathVariable Long userId) {
        StatisticsInfo statisticsInfo = mallPayService.getStatisticsByUserId(userId);
        return Result.success(statisticsInfo);
    }
}
