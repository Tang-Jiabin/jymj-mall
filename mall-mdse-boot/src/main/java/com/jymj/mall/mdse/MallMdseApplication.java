package com.jymj.mall.mdse;

import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.search.api.MdseSearchFeignClient;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.user.api.UserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableCaching
@EnableAsync
@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients(clients = {DeptFeignClient.class, MallFeignClient.class,ShopFeignClient.class, MdseSearchFeignClient.class, UserFeignClient.class})
public class MallMdseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMdseApplication.class, args);
    }

}
