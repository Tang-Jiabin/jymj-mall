package com.jymj.mall.shop.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.shop.vo.ShopInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

/**
 * 店铺Client
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@FeignClient(value = "mall-shop", contextId = "mall-shop")
public interface ShopFeignClient {

    @GetMapping("/api/v1/shop/lists")
    Result<List<ShopInfo>> lists();

    @GetMapping("/api/v1/shop/{ids}")
    Result<List<ShopInfo>> getAllById(@PathVariable String ids);

    @GetMapping("/api/v1/shop/{shopId}/info")
    @Cacheable(cacheNames = "mall-shop:shop-info:", key = "'shop-id:'+#shopId")
    Result<ShopInfo> getShopById(@Valid @PathVariable Long shopId);
}
