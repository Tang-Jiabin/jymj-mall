package com.jymj.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云短信配置
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-14
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.sms")
public class TencentSMSProperties {

    private String appid;
    private String secretId;
    private String secretKey;
    private String sign;
    private String codeTemplateId;
    private String noticeTemplateId;
    private String url;

}
