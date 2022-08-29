package com.jymj.mall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;



@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients
public class MallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallUserApplication.class, args);
    }

}
