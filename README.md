# 金域美境 - 點亮鄉村

## 微服务商城服务端

### 项目简介

[jymj-mall](https://gitee.com/J_Tang/jymj-mall) 是基于Spring Boot 2.7、Spring Cloud 2021 & Alibaba 2021等主流技术栈构建的商城项目

### 技术选型

| 技术                           | 版本            | 说明           |
|:-----------------------------|:--------------|:-------------|
| SpringBoot                   | 2.7.1         | 应用框架         |
| SpringCloud                  | 2021.0.3      | 微服务框架        |
| SpringCloud Gateway          | 2021.0.3      | 微服务网关        |
| SpringCloud OpenFeign        | 2021.0.3      | RPC 调用       |
| SpringCloud Alibaba Nacos    | 2021.1        | 注册中心、配置中心    |
| SpringCloud Alibaba Seata    | 2021.1        | 分布式事务        |
| SpringCloud Alibaba Sentinel | 2021.1        | 流量控制、熔断降级    |
| SpringBoot JPA               | 2.7.1         | 持久化ORM框架     |
| SpringBoot Admin             | 2.7.1         | 服务监控         |
| Spring Cache                 | 2.7.1         | 缓存框架         |
| Spring Security OAuth2       | 2.2.5.RELEASE | 认证和授权框架      |
| Open ZipKin                  | 2.2.8.RELEASE | 链路追踪         |
| RabbitMQ                     | 3.11.1        | 消息队列         |
| Redisson                     | 3.17.7        | 分布式锁         |
| Elasticsearch                | 7.17.4        | 分布式搜索和分析引擎   |
| Aliyun OSS                   | 3.10.2        | 文件存储         |
| Redis                        | 7.0.4         | key-value数据库 |
| PostgreSQL                   | 14.5          | 关系型数据库       |

### 模块说明

| 模块名               | 说明                  |
|:------------------|:--------------------|
| mall-admin        | 商城管理服务              |
| mall-common       | 系统常量、枚举、通用工具类       |
| mall-common-redis | redis、redisson配置工具类 |
| mall-common-web   | WebMVC通用拦截器异常处理等    |
| mall-oauth        | 授权、鉴权服务             |
| mall-xxx-api      | 对应服务OpenFeign接口     |
| mall-file         | 文件服务                |
| mall-gateway      | 网关服务                |
| mall-mdse         | 商品服务                |
| mall-monitor      | 监控服务                |
| mall-order        | 订单服务                |
| mall-pay          | 支付服务                |
| mall-search       | 搜索服务                |
| mall-shop         | 店铺服务                |
| mall-user         | 用户服务                |

### 部署说明
124.221.102.62
