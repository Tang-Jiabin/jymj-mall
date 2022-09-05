package com.jymj.mall.file.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * 阿里云角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-05
 */
@Data
public class AliRole {

    @Value("${aliyun.role.arn}")
    private String arn;

    @Value("${aliyun.role.session-name}")
    private String sessionName;
}
