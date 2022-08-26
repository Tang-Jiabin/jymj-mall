package com.jymj.mall.user.controller;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Api(tags = "用户")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @ApiIgnore
    @GetMapping("/{username}")
    public Result<AdminAuthDTO> loadUserByUsername(@PathVariable String username) {
        System.out.println(username);
        AdminAuthDTO adminAuthDTO = new AdminAuthDTO();
        adminAuthDTO.setUserId(1L);
        adminAuthDTO.setUsername("user");
        adminAuthDTO.setPassword("{bcrypt}$2a$10$QiELi3znZzdlR9tNPfObju74UmG5iVaeyBX4c45TY.J.NbkMJI8H6");
        adminAuthDTO.setStatus(1);
        adminAuthDTO.setRoles(Lists.newArrayList("ROOT", "ADMIN"));
        adminAuthDTO.setDeptId(1L);
        return Result.success(adminAuthDTO);
    }


}
