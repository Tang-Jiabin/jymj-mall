package com.jymj.mall.gateway.filter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网关请求响应日志打印
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@ConditionalOnProperty(
        prefix = "log",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true  // true表示缺少log.enabled属性也会加载该bean
)
@Component
@Slf4j
public class GatewayLogFilter implements GlobalFilter, Ordered {

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //优化下面代码
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().pathWithinApplication().value();
        String requestMethod = request.getMethodValue();
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        TraceLog traceLog = new TraceLog();
        traceLog.setRequestPath(requestPath);
        traceLog.setRequestMethod(requestMethod);
        traceLog.setRequestTime(DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN));

        MediaType contentType = request.getHeaders().getContentType();

        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)
                || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)
                || MediaType.APPLICATION_JSON.isCompatibleWith(contentType)
        ) {
            return writeBodyLog(exchange, chain, traceLog);
        } else {
            return writeLog(exchange, chain, traceLog);
        }
    }

    /**
     * 无请求体请求日志输出
     *
     * @param exchange
     * @param chain
     * @param traceLog
     * @return
     */
    public Mono<Void> writeLog(ServerWebExchange exchange, GatewayFilterChain chain, TraceLog traceLog) {
        StringBuilder sb = new StringBuilder();
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();

        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String val = entry.getValue().stream().map(String::valueOf).collect(Collectors.joining(","));
            sb.append(entry.getKey()).append("=").append(val).append("&");
        }
        if (sb.length() > 0) {
            traceLog.setQueryParams(sb.substring(0, sb.length() - 1));
        }
//        log.info(traceLog.toRequestString());
        ServerHttpResponseDecorator serverHttpResponseDecorator = serverHttpResponseDecorator(exchange, traceLog);
        return chain.filter(exchange.mutate().response(serverHttpResponseDecorator)
                        .build())
                .then(Mono.fromRunnable(() -> log.info(traceLog.toString())));
    }


    /**
     * 有请求体请求日志输出
     *
     * @param exchange
     * @param chain
     * @param traceLog
     * @return
     */
    public Mono writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, TraceLog traceLog) {

        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);

        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String val = entry.getValue().stream().map(String::valueOf).collect(Collectors.joining(","));
            sb.append(entry.getKey()).append("=").append(val).append("&");
        }
        if (sb.length() > 0) {
            traceLog.setQueryParams(sb.substring(0, sb.length() - 1));
        }

        Mono<String> cachedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            traceLog.setRequestBody(body);
            return Mono.just(body);
        });
//                .doFinally(body -> log.info(traceLog.toRequestString()));

        BodyInserter bodyInserter = BodyInserters.fromPublisher(cachedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequest serverHttpRequest = serverHttpRequestDecorator(exchange, headers, outputMessage);
                    ServerHttpResponseDecorator serverHttpResponseDecorator = serverHttpResponseDecorator(exchange, traceLog);
                    return chain.filter(exchange.mutate().request(serverHttpRequest).response(serverHttpResponseDecorator).build())
                            .then(Mono.fromRunnable(() -> log.info(traceLog.toString())));
                }));
    }


    private ServerHttpRequestDecorator serverHttpRequestDecorator(ServerWebExchange exchange,
                                                                  HttpHeaders headers,
                                                                  CachedBodyOutputMessage outputMessage
    ) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                httpHeaders.setContentLength(headers.getContentLength());
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }


    /**
     * 响应结果读取
     *
     * @param exchange
     * @param traceLog
     * @return
     */
    private ServerHttpResponseDecorator serverHttpResponseDecorator(ServerWebExchange exchange, TraceLog traceLog) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        return new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Date now = new Date();
                    String responseTime = DateUtil.format(now, DatePattern.NORM_DATETIME_MS_PATTERN);
                    traceLog.setResponseTime(responseTime);
                    long executeTime = DateUtil.betweenMs(DateUtil.parse(traceLog.getRequestTime(), DatePattern.NORM_DATETIME_MS_FORMATTER), now);
                    traceLog.setExecuteTime(executeTime);

                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                    if (StrUtil.isNotBlank(originalResponseContentType)
                            && originalResponseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {

                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                            DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().join(dataBuffers);

                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);

                            DataBufferUtils.release(dataBuffer);

                            String responseBody = new String(content, StandardCharsets.UTF_8);
                            traceLog.setResponseBody(responseBody);
                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                return super.writeWith(body);
            }
        };
    }

    @Override
    public int getOrder() {
        return -1;
    }

    // 多次反向代理后会有多个ip值 的分割符
    private static final String IP_UTILS_FLAG = ",";
    // 未知IP
    private static final String UNKNOWN = "unknown";
    // 本地 IP
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IP1 = "127.0.0.1";

    private static String getIP(ServerHttpRequest request) {
        // 根据 HttpHeaders 获取 请求 IP地址
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("x-forwarded-for");
            if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ip.contains(IP_UTILS_FLAG)) {
                    ip = ip.split(IP_UTILS_FLAG)[0];
                }
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        //兼容k8s集群获取ip
        if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
            if (LOCALHOST_IP1.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                //根据网卡取本机配置的IP
                InetAddress iNet = null;
                try {
                    iNet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("getClientIp error: ", e);
                }
                ip = iNet.getHostAddress();
            }
        }
        return ip;
    }

    @Data
    public static class TraceLog {

        /**
         * 请求路径
         */
        private String requestPath;

        /**
         * 请求方法
         */
        private String requestMethod;

        /**
         * 查询参数
         */
        private String queryParams;

        /**
         * 请求载荷
         */
        private String requestBody;

        /**
         * 响应数据
         */
        private String responseBody;

        /**
         * 请求时间
         */
        private String requestTime;

        /**
         * 响应时间
         */
        private String responseTime;

        /**
         * 执行耗时(毫秒)
         */
        private Long executeTime;


        public String toRequestString() {
            return
                    System.lineSeparator() + "请求日志: " + requestMethod + ':' + requestPath + System.lineSeparator() +
                            "查询参数:" + queryParams + System.lineSeparator() +
                            "请求载荷:" + requestBody + System.lineSeparator() +
                            "请求时间:" + requestTime;
        }

        public String toResponseString() {
            return
                    System.lineSeparator() + "响应日志: " + requestMethod + ':' + requestPath + System.lineSeparator() +
                            "请求时间:" + requestTime + System.lineSeparator() +
                            "响应时间:" + responseTime + System.lineSeparator() +
                            "响应数据:" + responseBody + System.lineSeparator() +
                            "执行耗时:" + executeTime + "毫秒";
        }

        @Override
        public String toString() {
            return System.lineSeparator() +
                    "================================  网关请求响应日志  ===============================" + System.lineSeparator() +
                    "请求路径:" + requestPath + System.lineSeparator() +
                    "请求方法:" + requestMethod + System.lineSeparator() +
                    "查询参数:" + queryParams + System.lineSeparator() +
                    "请求参数:" + requestBody + System.lineSeparator() +
                    "响应数据:" + responseBody + System.lineSeparator() +
                    "请求时间:" + requestTime + System.lineSeparator() +
                    "响应时间:" + responseTime + System.lineSeparator() +
                    "执行耗时:" + executeTime + "毫秒" + System.lineSeparator() +
                    "==================================================================================" + System.lineSeparator();
        }
    }
}
