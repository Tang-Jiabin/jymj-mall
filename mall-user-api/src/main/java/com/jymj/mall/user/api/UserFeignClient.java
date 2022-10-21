package com.jymj.mall.user.api;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.user.api.fallback.UserFeignFallbackClient;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 用户
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@FeignClient(value = "mall-user", fallback = UserFeignFallbackClient.class)
public interface UserFeignClient {


    @GetMapping("/api/v1/users/load/{openId}")
    Result<UserAuthDTO> loadUserByOpenId(@PathVariable String openId);

    @PostMapping("/api/v1/users")
    void addUser(UserDTO userDTO);
}
