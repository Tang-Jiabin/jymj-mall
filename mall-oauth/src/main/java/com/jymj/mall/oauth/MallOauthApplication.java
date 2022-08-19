package com.jymj.mall.oauth;

import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.user.api.UserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall.*"})
@EnableFeignClients(clients = {AdminFeignClient.class, UserFeignClient.class})
public class MallOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOauthApplication.class, args);
    }

}
