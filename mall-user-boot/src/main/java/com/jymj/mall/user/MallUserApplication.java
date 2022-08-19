package com.jymj.mall.user;

import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.api.DeptFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients
public class MallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallUserApplication.class, args);
    }

}
