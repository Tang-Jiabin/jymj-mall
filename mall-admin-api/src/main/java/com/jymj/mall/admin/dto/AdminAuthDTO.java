package com.jymj.mall.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户授权
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Data
public class AdminAuthDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户状态(1:正常;0:禁用)
     */
    private Integer status;

    /**
     * 用户角色编码集合 ["ROOT","ADMIN"]
     */
    private List<String> roles;

    /**
     * 部门ID
     */
    private Long deptId;

}
