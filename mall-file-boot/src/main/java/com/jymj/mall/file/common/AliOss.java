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
public class AliOss {
    /**
     * 区域id
     */
    @Value("${aliyun.oss.region-id}")
    private String regionId;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    /**过期时间*/
    @Value("${aliyun.oss.expireTime}")
    private Long expireTime;

    /**
     * 文件最小
     */
    @Value("${aliyun.oss.min}")
    private Long min;

    /**
     * 文件最大
     */
    @Value("${aliyun.oss.max}")
    private Long max;

    /**
     * 目录
     */
    @Value("${aliyun.oss.dir}")
    private String dir;

    /**
     * 直传地址
     */
    @Value("${aliyun.oss.host}")
    private String host;
}
