package com.jymj.mall.shop;

import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.api.DistrictFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-18
 */

@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients(clients = {AdminFeignClient.class, DeptFeignClient.class, DistrictFeignClient.class})
public class MallShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallShopApplication.class, args);
    }
}
