package com.jymj.mall.admin.component;

import com.jymj.mall.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 容器启动完成时加载角色权限规则至Redis缓存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Component
@RequiredArgsConstructor
public class InitPermissionRolesCache implements CommandLineRunner {

    private final PermissionService permissionService;

    @Override
    public void run(String... args) {
        permissionService.refreshPermRolesRules();
    }
}
