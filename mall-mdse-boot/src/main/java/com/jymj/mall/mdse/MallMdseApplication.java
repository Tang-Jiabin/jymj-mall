package com.jymj.mall.mdse;

import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.api.DistrictFeignClient;
import com.jymj.mall.admin.api.PermissionFeignClient;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.api.ShopFeignClient;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 商品类
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */

@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients(clients = {DeptFeignClient.class, MallFeignClient.class,ShopFeignClient.class})
public class MallMdseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMdseApplication.class, args);
    }

}
