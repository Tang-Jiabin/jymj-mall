package com.jymj.mall.oauth.security.config;


import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.oauth.security.core.clientdetails.ClientDetailsServiceImpl;
import com.jymj.mall.oauth.security.core.userdetails.admin.SysAdminDetails;
import com.jymj.mall.oauth.security.core.userdetails.admin.SysAdminDetailsServiceImpl;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetails;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetailsServiceImpl;
import com.jymj.mall.oauth.security.extension.captcha.CaptchaTokenGranter;
import com.jymj.mall.oauth.security.extension.mobile.SmsCodeTokenGranter;
import com.jymj.mall.oauth.security.extension.refresh.PreAuthenticatedUserDetailsServiceImpl;
import com.jymj.mall.oauth.security.extension.wechat.WechatTokenGranter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ?????????????????????
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Slf4j
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
        log.info("load clients");
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * ???????????????authorization??????????????????token?????????????????????????????????(token services)
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Token??????
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = Lists.newArrayList();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        //token?????????????????? ?????????InMemoryTokenStore????????????????????????
        endpoints.tokenStore(jwtTokenStore());

        // ??????????????????????????????(???????????????????????????????????????????????????????????????)????????????
        List<TokenGranter> granterList = Lists.newArrayList(Collections.singletonList(endpoints.getTokenGranter()));

        // ????????????????????????????????????
        granterList.add(new CaptchaTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager, stringRedisTemplate
        ));

        // ???????????????????????????????????????????????????
        granterList.add(new SmsCodeTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager
        ));

        // ????????????????????????????????????
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
     * jwt token????????????
     */
    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    public DefaultTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = Lists.newArrayList();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        // ???????????????????????????token?????????????????????ID??? UserDetailService ?????????Map
        Map<String, UserDetailsService> clientUserDetailsServiceMap = Maps.newHashMap();
        // ?????????????????????
        clientUserDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID, sysAdminDetailsService);
        // Android???????????????
        clientUserDetailsServiceMap.put(SecurityConstants.APP_ANDROID_CLIENT_ID, sysUserDetailsService);
        // ????????????????????????
        clientUserDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID, sysUserDetailsService);

        // ??????token?????????????????????????????????????????????AuthenticationManager??????????????????????????????ID?????????????????????????????????????????????????????????
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedUserDetailsServiceImpl<>(clientUserDetailsServiceMap));
        tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));

        /* refresh_token????????????????????????????????????(true)??????????????????(false)????????????true
           1 ???????????????access_token?????????????????? refresh_token?????????????????????????????????????????????????????????
           2 ??????????????????access_token?????????????????? refresh_token????????????????????????refresh_token??????????????????????????????????????????????????????????????????
         */
        tokenServices.setReuseRefreshToken(false);
        return tokenServices;

    }


    /**
     * ??????????????????????????????token??????
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * ???????????????????????????(??????+??????)
     */
    @Bean
    public KeyPair keyPair() {
        ClassPathResource classPathResource = new ClassPathResource("jwt.jks");
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "jymjshop".toCharArray());
        KeyPair keyPair = factory.getKeyPair("jymj", "jymjshop".toCharArray());
        return keyPair;
    }

    /**
     * JWT????????????
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = Maps.newHashMap();
            Object principal = authentication.getUserAuthentication().getPrincipal();
            if (principal instanceof SysAdminDetails) {
                SysAdminDetails sysAdminDetails = (SysAdminDetails) principal;
                additionalInfo.put("adminId", sysAdminDetails.getUserId());
                additionalInfo.put("username", sysAdminDetails.getUsername());
                additionalInfo.put("deptId", sysAdminDetails.getDeptId());
                // ??????????????????(username:????????????)
                if (StringUtils.hasText(sysAdminDetails.getAuthenticationIdentity())) {
                    additionalInfo.put("authenticationIdentity", sysAdminDetails.getAuthenticationIdentity());
                }
            } else if (principal instanceof SysUserDetails) {
                SysUserDetails sysUserDetails = (SysUserDetails) principal;
                additionalInfo.put("userId", sysUserDetails.getMemberId());
                if (StringUtils.hasText(sysUserDetails.getUsername())) {
                    additionalInfo.put("nickname", sysUserDetails.getNickname());
                }
                if (StringUtils.hasText(sysUserDetails.getSessionKey())) {
                    additionalInfo.put("sessionKey", sysUserDetails.getSessionKey());
                }
                if (StringUtils.hasText(sysUserDetails.getOpenId())) {
                    additionalInfo.put("openId", sysUserDetails.getOpenId());
                }
                // ??????????????????(mobile:????????????openId:???????????????????????????????????????)
                if (StringUtils.hasText(sysUserDetails.getAuthenticationIdentity())) {
                    additionalInfo.put("authenticationIdentity", sysUserDetails.getAuthenticationIdentity());
                }
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }


    /**
     * ?????????????????????????????????
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
