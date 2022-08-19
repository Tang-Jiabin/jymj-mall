package com.jymj.mall.common.web.security.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePerms {

    String[] value() default {};

}

