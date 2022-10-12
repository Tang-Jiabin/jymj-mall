package com.jymj.mall.oauth.security.extension.wechat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WechatAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 550L;
    private final Object principal;
    @Getter
    private String encryptedData;
    @Getter
    private String iv;

    /**
     * 账号校验之前的token构建
     *
     * @param principal
     */
    public WechatAuthenticationToken(Object principal, String encryptedData, String iv) {
        super(null);
        this.principal = principal;
        this.encryptedData = encryptedData;
        this.iv = iv;
        setAuthenticated(false);
    }

    /**
     * 账号校验成功之后的token构建
     *
     * @param principal
     * @param authorities
     */
    public WechatAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
