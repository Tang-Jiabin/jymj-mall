package com.jymj.mall.gateway.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.GlobalConstants;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 网关自定义鉴权管理器
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceServerManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RedisUtils redisUtils;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {

        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        // 预检请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        PathMatcher pathMatcher = new AntPathMatcher();
        String method = request.getMethodValue();
        String path = request.getURI().getPath();
        String restfulPath = method + ":" + path;

        // 如果token以"bearer "为前缀，到此方法里说明JWT有效即已认证
//        String token = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION_KEY);

//        if (StrUtil.isNotBlank(token) && StrUtil.startWithIgnoreCase(token, SecurityConstants.JWT_PREFIX)) {
//
//        } else {
//
//            return Mono.just(new AuthorizationDecision(false));
//        }


        // 鉴权开始 缓存取 [URL权限-角色集合] 规则数据
        Map<Object, Object> urlPermRolesRules = redisUtils.hmget(GlobalConstants.URL_PERM_ROLES_KEY);
        // 根据请求路径获取有访问权限的角色列表
        // 拥有访问权限的角色
        List<String> authorizedRoles = Lists.newArrayList();
        // 是否需要鉴权，默认未设置拦截规则不需鉴权
        boolean requireCheck = false;

        for (Map.Entry<Object, Object> permRoles : urlPermRolesRules.entrySet()) {
            String perm = (String) permRoles.getKey();
            if (pathMatcher.match(perm, restfulPath)) {
                List<String> roles = Convert.toList(String.class, permRoles.getValue());
                authorizedRoles.addAll(roles);
                if (!requireCheck) {
                    requireCheck = true;
                }
            }
        }

        // 没有设置拦截规则拒绝
        if (!requireCheck) {
            if (!restfulPath.contains("actuator")) {
                log.info("没有设置拦截规则允许");
                log.info("访问路径方式 : {}", restfulPath);
            }
            return Mono.just(new AuthorizationDecision(true));
        }

        // 判断JWT中携带的用户角色是否有权限访问
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authority -> {
                    String roleCode = CharSequenceUtil.removePrefix(authority, SecurityConstants.AUTHORITY_PREFIX);// ROLE_ADMIN移除前缀ROLE_得到用户的角色编码ADMIN
                    if (GlobalConstants.ROOT_ROLE_CODE.equals(roleCode)) {
                        return true; // 如果是超级管理员则放行
                    }
                    return CollUtil.isNotEmpty(authorizedRoles) && authorizedRoles.contains(roleCode);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
