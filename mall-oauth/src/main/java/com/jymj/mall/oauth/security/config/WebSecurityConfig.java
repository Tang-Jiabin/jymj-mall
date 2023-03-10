package com.jymj.mall.oauth.security.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.google.common.collect.Maps;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.oauth.security.core.MoreAuthenticatedUserDetailsServiceImpl;
import com.jymj.mall.oauth.security.extension.mobile.SmsCodeAuthenticationProvider;
import com.jymj.mall.oauth.security.extension.wechat.WechatAuthenticationProvider;
import com.jymj.mall.user.api.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;


@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService sysAdminDetailsService;
    private final UserDetailsService sysUserDetailsService;
    private final WxMaService wxMaService;
    private final UserFeignClient userFeignClient;
    private final StringRedisTemplate redisTemplate;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        Map<String, UserDetailsService> userDetailsServiceMap = Maps.newHashMap();
        userDetailsServiceMap.put(SecurityConstants.APP_ANDROID_CLIENT_ID, sysUserDetailsService);
        userDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID, sysUserDetailsService);
        userDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID, sysAdminDetailsService);
        authenticationProvider.setUserDetailsService(new MoreAuthenticatedUserDetailsServiceImpl(userDetailsServiceMap));
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/oauth/**","/oauth/token").permitAll()
                .antMatchers("/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

    /**
     * ??????????????????
     *
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(wechatAuthenticationProvider()).
                authenticationProvider(daoAuthenticationProvider()).
                authenticationProvider(smsCodeAuthenticationProvider());
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @Bean
    public SmsCodeAuthenticationProvider smsCodeAuthenticationProvider() {
        SmsCodeAuthenticationProvider provider = new SmsCodeAuthenticationProvider();
        provider.setUserDetailsService(sysUserDetailsService);
        provider.setAdminDetailsService(sysAdminDetailsService);
        provider.setRedisTemplate(redisTemplate);
        return provider;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    @Bean
    public WechatAuthenticationProvider wechatAuthenticationProvider() {

        WechatAuthenticationProvider provider = new WechatAuthenticationProvider();
        provider.setUserDetailsService(sysUserDetailsService);
        provider.setWxMaService(wxMaService);
        provider.setUserFeignClient(userFeignClient);
        return provider;
    }


    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        Map<String, UserDetailsService> userDetailsServiceMap = Maps.newHashMap();
        userDetailsServiceMap.put(SecurityConstants.APP_ANDROID_CLIENT_ID, sysUserDetailsService);
        userDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID, sysUserDetailsService);
        userDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID, sysAdminDetailsService);
        provider.setUserDetailsService(new MoreAuthenticatedUserDetailsServiceImpl(userDetailsServiceMap));
        provider.setPasswordEncoder(passwordEncoder());
        // ??????????????????????????????????????????:true-?????????false-???????????????
        provider.setHideUserNotFoundExceptions(true);
        return provider;
    }


    /**
     * ???????????????
     * <p>
     * ???????????????????????????????????????????????????encoder????????????{bcypt}??????->??????BCYPT???????????????{noop}->?????????????????????????????????????????????
     * ???????????? DaoAuthenticationProvider#additionalAuthenticationChecks
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
