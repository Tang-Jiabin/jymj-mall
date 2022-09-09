package com.jymj.mall.file.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-05
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliYunConfig {

    /**
     * 访问控制-人员管理-用户 AccessKey
     */
    private String accessKey;

    /**
     * 访问控制-人员管理-用户 secret
     */
    private String secret;

    /**
     * 阿里对象存储
     */
    private AliOss oss;

    private AliRole role;

}
