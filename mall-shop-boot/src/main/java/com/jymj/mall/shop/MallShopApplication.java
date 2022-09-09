package com.jymj.mall.shop;

import com.jymj.mall.admin.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-18
 */

@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients(clients = {AdminFeignClient.class, DeptFeignClient.class, DistrictFeignClient.class, PermissionFeignClient.class, RoleFeignClient.class})
public class MallShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallShopApplication.class, args);
    }
}
