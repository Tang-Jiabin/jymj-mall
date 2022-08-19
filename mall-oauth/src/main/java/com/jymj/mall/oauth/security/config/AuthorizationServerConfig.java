package com.jymj.mall.oauth.security.config;


import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.oauth.security.core.clientdetails.ClientDetailsServiceImpl;
import com.jymj.mall.oauth.security.core.userdetails.admin.SysAdminDetails;
import com.jymj.mall.oauth.security.core.userdetails.admin.SysAdminDetailsServiceImpl;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetails;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetailsServiceImpl;
import com.jymj.mall.oauth.security.extension.captcha.CaptchaTokenGranter;
import com.jymj.mall.oauth.security.extension.mobile.SmsCodeTokenGranter;
import com.jymj.mall.oauth.security.extension.refresh.PreAuthenticatedUserDetailsService;
import com.jymj.mall.oauth.security.extension.wechat.WechatTokenGranter;
import com.jymj.mall.common.result.ResultCode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.util.*;

/**
 * 认证服务器配置
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final ClientDetailsServiceImpl clientDetailsService;
    private final SysAdminDetailsServiceImpl sysAdminDetailsService;
    private final SysUserDetailsServiceImpl sysUserDetailsService;
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.withClientDetails((ClientDetailsService) clientDetailsService);
    }

    /**
     * 配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Token增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = Lists.newArrayList();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        //token存储模式设定 默认为InMemoryTokenStore模式存储到内存中
        endpoints.tokenStore(jwtTokenStore());

        // 获取原有默认授权模式(授权码模式、密码模式、客户端模式、简化模式)的授权者
        List<TokenGranter> granterList = Lists.newArrayList(Collections.singletonList(endpoints.getTokenGranter()));

        // 添加验证码授权模式授权者
        granterList.add(new CaptchaTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager, stringRedisTemplate
        ));

        // 添加手机短信验证码授权模式的授权者
        granterList.add(new SmsCodeTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager
        ));

        // 添加微信授权模式的授权者
        granterList.add(new WechatTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager
        ));

        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(granterList);
        endpoints
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain)
                .tokenGranter(compositeTokenGranter)
                .tokenServices(tokenServices(endpoints));
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        /*security.allowFormAuthenticationForClients();*/
        CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(security);
        endpointFilter.afterPropertiesSet();
        endpointFilter.setAuthenticationEntryPoint(authenticationEntryPoint());
        security.addTokenEndpointAuthenticationFilter(endpointFilter);

        security.authenticationEntryPoint(authenticationEntryPoint())
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()")
                .addTokenEndpointAuthenticationFilter(endpointFilter);
    }


    /**
     * jwt token存储模式
     */
    @Bean
    public JwtTokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    public DefaultTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers =Lists.newArrayList();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService((ClientDetailsService) clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        // 多用户体系下，刷新token再次认证客户端ID和 UserDetailService 的映射Map
        Map<String, UserDetailsService> clientUserDetailsServiceMap = new HashMap<>();
        clientUserDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID, sysAdminDetailsService); // 系统管理客户端
        clientUserDetailsServiceMap.put(SecurityConstants.APP_CLIENT_ID, sysUserDetailsService); // Android、IOS、H5 移动客户端
        clientUserDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID, sysUserDetailsService); // 微信小程序客户端

        // 刷新token模式下，重写预认证提供者替换其AuthenticationManager，可自定义根据客户端ID和认证方式区分用户体系获取认证用户信息
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedUserDetailsService<>(clientUserDetailsServiceMap));
        tokenServices.setAuthenticationManager(new ProviderManager(Arrays.asList(provider)));

        /** refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
         *  1 重复使用：access_token过期刷新时， refresh_token过期时间未改变，仍以初次生成的时间为准
         *  2 非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新便永不失效达到无需再次登录的目的
         */
        tokenServices.setReuseRefreshToken(true);
        return tokenServices;

    }



    /**
     * 使用非对称加密算法对token签名
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * 密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        ClassPathResource classPathResource = new ClassPathResource("jwt.jks");

        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "jymjshop".toCharArray());
        KeyPair keyPair = factory.getKeyPair("jymj", "jymjshop".toCharArray());
        return keyPair;
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = new HashMap<>();
            Object principal = authentication.getUserAuthentication().getPrincipal();
            if (principal instanceof SysAdminDetails) {
                SysAdminDetails sysAdminDetails = (SysAdminDetails) principal;
                additionalInfo.put("adminId", sysAdminDetails.getUserId());
                additionalInfo.put("username", sysAdminDetails.getUsername());
                additionalInfo.put("deptId", sysAdminDetails.getDeptId());
                // 认证身份标识(username:用户名；)
                if (StringUtils.hasText(sysAdminDetails.getAuthenticationIdentity())) {
                    additionalInfo.put("authenticationIdentity", sysAdminDetails.getAuthenticationIdentity());
                }
            } else if (principal instanceof SysUserDetails) {
                SysUserDetails sysUserDetails = (SysUserDetails) principal;
                additionalInfo.put("userId", sysUserDetails.getMemberId());
                additionalInfo.put("username", sysUserDetails.getUsername());
                // 认证身份标识(mobile:手机号；openId:开放式认证系统唯一身份标识)
                if (StringUtils.hasText(sysUserDetails.getAuthenticationIdentity())) {
                    additionalInfo.put("authenticationIdentity", sysUserDetails.getAuthenticationIdentity());
                }
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }


    /**
     * 自定义认证异常响应数据
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setStatus(HttpStatus.OK.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            Result result = Result.failed(ResultCode.CLIENT_AUTHENTICATION_FAILED);
            response.getWriter().print(JSON.toJSONString(result));
            response.getWriter().flush();
        };
    }
}
