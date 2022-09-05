package com.jymj.mall.file.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * 阿里oss
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-05
 */
@Data
public class AliOSS {
    /**
     * 区域id
     */
    @Value("${aliyun.oss.region-id}")
    private String regionId;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
}
