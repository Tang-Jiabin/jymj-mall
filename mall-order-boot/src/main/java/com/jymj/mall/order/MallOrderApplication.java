package com.jymj.mall.order;

import com.jymj.mall.marketing.api.CouponFeignClient;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.api.MdseStockFeignClient;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.user.api.UserAddressFeignClient;
import com.jymj.mall.user.api.UserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 商城订单购物车服务
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@EnableAsync
@EnableCaching
@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients(clients = {ShopFeignClient.class, MdseFeignClient.class, MdseStockFeignClient.class, UserAddressFeignClient.class, UserFeignClient.class, CouponFeignClient.class})
public class MallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class, args);
    }
}
