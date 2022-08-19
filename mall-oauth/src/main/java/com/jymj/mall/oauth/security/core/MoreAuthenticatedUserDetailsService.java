package com.jymj.mall.oauth.security.core;

import com.jymj.mall.oauth.utils.RequestUtils;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 多类型用户
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-11
 */
@Service("moreUserDetailsService")
@NoArgsConstructor
public class MoreAuthenticatedUserDetailsService implements UserDetailsService {

    private Map<String, UserDetailsService> userDetailsServiceMap;

    public MoreAuthenticatedUserDetailsService(Map<String, UserDetailsService> userDetailsServiceMap) {
        Assert.notNull(userDetailsServiceMap, "userDetailsService cannot be null.");
        this.userDetailsServiceMap = userDetailsServiceMap;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = RequestUtils.getOAuth2ClientId();
        Assert.notNull(clientId, "clientId cannot be null.");
        UserDetailsService userDetailsService = userDetailsServiceMap.get(clientId);
        return userDetailsService.loadUserByUsername(username);

    }
}
