package com.jymj.mall.mdse.config;

import org.elasticsearch.index.VersionType;
import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * ES文档
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-19
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Document  {

    // 索引库的名称，个人建议以项目的名称命名
    String indexName();

    //默认分区数
    short shards() default 1;

    // 每个分区默认的备份数
    short replicas() default 1;

    // 刷新间隔
    String refreshInterval() default "1s";

    // 索引文件存储类型
    String indexStoreType() default "fs";

    // 是否创建索引
    boolean createIndex() default true;

    // 版本
    VersionType versionType() default VersionType.EXTERNAL;

}
