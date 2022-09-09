package com.jymj.mall.oauth.security.core.userdetails.user;


import com.jymj.mall.common.enums.AuthenticationIdentityEnum;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Slf4j
@Service("sysUserDetailsService")
@RequiredArgsConstructor
public class SysUserDetailsServiceImpl implements UserDetailsService {

    private final UserFeignClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUserDetails userDetails = null;
        Result<UserAuthDTO> result = userFeignClient.loadUserByUsername(username);
        log.info("loadUser:{}",result.toString());
        if (Result.isSuccess(result)) {
            UserAuthDTO user = result.getData();
            if (null != user) {
                userDetails = new SysUserDetails(user);
            }
        }
        return getUserDetails(userDetails);
    }


    /**
     * 手机号码认证方式
     *
     */
    public UserDetails loadUserByMobile(String mobile) {
        SysUserDetails sysUserDetails = null;
        Result<UserAuthDTO> result = userFeignClient.loadUserByUsername(mobile);
        if (Result.isSuccess(result)) {
            UserAuthDTO member = result.getData();
            if (null != member) {
                sysUserDetails = new SysUserDetails(member);
                // 认证身份标识:mobile
                sysUserDetails.setAuthenticationIdentity(AuthenticationIdentityEnum.MOBILE.getValue());
            }
        }
        return getUserDetails(sysUserDetails);
    }

    /**
     * openid 认证方式
     *
     */
    public UserDetails loadUserByOpenId(String openId) {
        SysUserDetails sysUserDetails = null;
        Result<UserAuthDTO> result = userFeignClient.loadUserByOpenId(openId);

        if (Result.isSuccess(result)) {
            UserAuthDTO member = result.getData();
            if (null != member) {
                sysUserDetails = new SysUserDetails(member);
                // 认证身份标识:openId
                sysUserDetails.setAuthenticationIdentity(AuthenticationIdentityEnum.OPENID.getValue());
            }
        }
        return getUserDetails(sysUserDetails);
    }

    private UserDetails getUserDetails(SysUserDetails sysUserDetails) {
        if (sysUserDetails == null) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMsg());
        } else if (!sysUserDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!sysUserDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!sysUserDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return sysUserDetails;
    }

}
