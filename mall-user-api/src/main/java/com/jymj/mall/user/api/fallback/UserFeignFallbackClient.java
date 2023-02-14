package com.jymj.mall.user.api.fallback;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.vo.MemberInfo;
import com.jymj.mall.user.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Component
@Slf4j
public class UserFeignFallbackClient implements UserFeignClient {

    @Override
    public Result<UserAuthDTO> loadUserByOpenId(String openId) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public void addUser(UserDTO userDTO) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
    }

    @Override
    public Result<UserInfo> getUserById(Long userId) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<UserInfo> updateUser(UserDTO userDTO) {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<MemberInfo> getMemberByUserId(Long userId) {
        return Result.failed(ResultCode.USER_NOT_EXIST);
    }

    @Override
    public Result<UserAuthDTO> loadUserByUsername(String username) {
        return Result.failed(ResultCode.DEGRADATION);
    }

    @Override
    public Result<UserAuthDTO> loadUserByMobile(String mobile) {
        return Result.failed(ResultCode.DEGRADATION);
    }


}
