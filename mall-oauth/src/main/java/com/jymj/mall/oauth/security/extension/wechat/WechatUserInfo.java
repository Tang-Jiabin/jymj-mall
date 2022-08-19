package com.jymj.mall.oauth.security.extension.wechat;

import lombok.Data;

/**
 * 微信用户信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Data
public class WechatUserInfo {
    private String avatarUrl;

    private String city;

    private String country;

    private Integer gender;

    private String language;

    private String nickName;

    private String province;

}
