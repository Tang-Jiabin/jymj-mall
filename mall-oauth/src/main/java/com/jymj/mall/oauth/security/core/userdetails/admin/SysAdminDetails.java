package com.jymj.mall.oauth.security.core.userdetails.admin;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.common.constants.GlobalConstants;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 系统用户信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Data
public class SysAdminDetails implements UserDetails {
    /**
     * 扩展字段：用户ID
     */
    private Long userId;

    /**
     * 扩展字段：认证身份标识
     */
    private String authenticationIdentity;

    /**
     * 扩展字段：部门ID
     */
    private Long deptId;

    /**
     * 默认字段
     */
    private String username;
    private String password;
    private Boolean enabled;
    private Collection<SimpleGrantedAuthority> authorities;

    /**
     * 系统管理用户
     */
    public SysAdminDetails(AdminAuthDTO user) {
        this.setUserId(user.getUserId());
        this.setUsername(user.getUsername());
        this.setDeptId(user.getDeptId());
        this.setPassword(user.getPassword());
        this.setEnabled(GlobalConstants.STATUS_YES.equals(user.getStatus()));
        if (user.getRoles() != null && user.getRoles().size() > 0) {
            authorities = Lists.newArrayList();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
