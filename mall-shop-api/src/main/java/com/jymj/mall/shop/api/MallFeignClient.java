package com.jymj.mall.shop.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.shop.vo.MallInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 商场
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-08
 */
@FeignClient(value = "mall-shop", contextId = "mall-details")
public interface MallFeignClient {

    @GetMapping("/api/v1/mall/dept/{deptIds}")
    Result<List<MallInfo>> getMallByDeptIdIn(@PathVariable String deptIds);
}
