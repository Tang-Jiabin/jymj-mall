package com.jymj.mall.oauth.security.core.userdetails.user;

import com.jymj.mall.common.constants.GlobalConstants;
import com.jymj.mall.user.dto.UserAuthDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Data
public class SysUserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private Long memberId;
    private String username;
    private Boolean enabled;

    private String openId;

    private String sessionKey;

    private List<String> roles;
    private String password;
    /**
     * 扩展字段：认证身份标识
     */
    private String authenticationIdentity;

    /**
     * 小程序会员用户体系
     *
     * @param user 小程序会员用户认证信息
     */
    public SysUserDetails(UserAuthDTO user) {
        this.setMemberId(user.getUserId());
        this.setUsername(user.getUsername());
        this.setOpenId(user.getUsername());
        this.setPassword(user.getPassword());
        this.setRoles(user.getRoles());
        this.setEnabled(GlobalConstants.STATUS_YES.equals(user.getStatus()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
