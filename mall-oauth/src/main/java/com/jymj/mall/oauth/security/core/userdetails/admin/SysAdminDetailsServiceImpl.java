package com.jymj.mall.oauth.security.core.userdetails.admin;


import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
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
 * 系统用户
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Service("sysAdminDetailsService")
@Slf4j
@RequiredArgsConstructor
public class SysAdminDetailsServiceImpl implements UserDetailsService {

    private final AdminFeignClient adminFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysAdminDetails userDetails = null;
        Result<AdminAuthDTO> result = adminFeignClient.loadAdminByUsername(username);
        log.info("loadAdmin:{}",result.toString());
        if (Result.isSuccess(result)) {
            AdminAuthDTO user = result.getData();
            if (null != user) {
                userDetails = new SysAdminDetails(user);
            }
        }
        return getUserDetails(userDetails);
    }

    public UserDetails loadUserByMobile(String mobile) {
        SysAdminDetails userDetails = null;
        Result<AdminAuthDTO> result = adminFeignClient.loadAdminByMobile(mobile);
        log.info("loadAdminByMobile:{}",result.toString());
        if (Result.isSuccess(result)) {
            AdminAuthDTO user = result.getData();
            if (null != user) {
                userDetails = new SysAdminDetails(user);
            }
        }
        return getUserDetails(userDetails);
    }

    private UserDetails getUserDetails(SysAdminDetails userDetails) {
        if (userDetails == null) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMsg());
        } else if (!userDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return userDetails;
    }


}
