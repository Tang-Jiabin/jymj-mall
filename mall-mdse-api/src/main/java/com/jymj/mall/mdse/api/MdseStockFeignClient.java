package com.jymj.mall.mdse.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 商品库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-17
 */
@FeignClient(value = "mall-mdse", contextId = "mall-stock")
public interface MdseStockFeignClient {


    @PutMapping("/api/v1/stock/less")
    Result<Object> lessMdseStock(@RequestBody StockDTO stockDTO);
}
