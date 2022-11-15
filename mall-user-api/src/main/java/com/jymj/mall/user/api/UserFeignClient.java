package com.jymj.mall.user.api;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.user.api.fallback.UserFeignFallbackClient;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.vo.MemberInfo;
import com.jymj.mall.user.vo.UserInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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

    @Cacheable(cacheNames = "mall-user:user-info:", key = "'user-id:'+#userId")
    @GetMapping("/api/v1/users/{userId}/info")
    Result<UserInfo> getUserById(@PathVariable Long userId);

    @PutMapping("/api/v1/users")
    Result<UserInfo> updateUser(@RequestBody UserDTO userDTO);

    @GetMapping("/api/v1/members/user/{userId}/info")
    Result<MemberInfo> getMemberByUserId(@PathVariable Long userId);
}
