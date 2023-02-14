package com.jymj.mall.oauth.security.extension.mobile;

import cn.hutool.core.util.StrUtil;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.oauth.security.core.userdetails.admin.SysAdminDetailsServiceImpl;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetailsServiceImpl;
import com.jymj.mall.oauth.utils.RequestUtils;
import com.jymj.mall.user.api.UserFeignClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

/**
 * 短信验证码认证授权提供者
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Slf4j
@Data
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private UserDetailsService adminDetailsService;
    private UserFeignClient userFeignClient;
    private StringRedisTemplate redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        // 测试环境不需要验证码
        String testCode = "666666";
        if (!testCode.equals(code)) {
            String codeKey = SecurityConstants.SMS_CODE_PREFIX + mobile;
            log.info("codeKey：{}", codeKey);
            String correctCode = redisTemplate.opsForValue().get(codeKey);
            log.info("验证码：{}，正确的验证码：{}", code, correctCode);
            // 验证码比对
            if (StrUtil.isBlank(correctCode) || !code.equals(correctCode)) {
                throw new BusinessException("验证码不正确");
            }
            // 比对成功删除缓存的验证码
            redisTemplate.delete(codeKey);
        }
        //获取客户端id
        String clientId = RequestUtils.getOAuth2ClientId();
        //根据clientId判断是用户端还是后台管理端
        UserDetails userDetails = null;
        if (clientId.equals("admin-web")) {
            userDetails = ((SysAdminDetailsServiceImpl) adminDetailsService).loadUserByMobile(mobile);
        } else {
            userDetails = ((SysUserDetailsServiceImpl) userDetailsService).loadUserByMobile(mobile);
        }

        SmsCodeAuthenticationToken result = new SmsCodeAuthenticationToken(userDetails, authentication.getCredentials(), new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
