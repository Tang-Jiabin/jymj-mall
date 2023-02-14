package com.jymj.mall.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Data
@Accessors(chain = true)
public class UserAuthDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会员名(openId、mobile)
     */
    private String username;
    /**
     * 微信openId
     */
    private String openId;

    private String nickname;

    /**
     * 状态(1:正常；0：禁用)
     */
    private Integer status;

    private String password;

    /**
     * 用户角色编码集合 ["ROOT","ADMIN"]
     */
    private List<String> roles;
}
