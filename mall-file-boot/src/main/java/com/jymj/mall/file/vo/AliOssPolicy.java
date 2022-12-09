package com.jymj.mall.file.vo;

import lombok.Data;

/**
 * AliOssPolicy
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-12-07
 */
@Data
public class AliOssPolicy {

    /**
     * 上传认证id
     */
    private String ossAccessKeyId;
    /**
     * policy
     */
    private String policy;
    /**
     * 签名
     */
    private String signature;
    /**
     * 直传文件的开头（路径）
     */
    private String dir;
    /**
     * 直传地址
     */
    private String host;
    /**
     * 上传截止时间
     */
    private String expire;
}
