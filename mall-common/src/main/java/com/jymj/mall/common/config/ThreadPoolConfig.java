package com.jymj.mall.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-12-09
 */
public class ThreadPoolConfig {

        /**
        * 核心线程数（默认线程数）
        */
        private int corePoolSize = 10;

        /**
        * 最大线程数
        */
        private int maxPoolSize = 200;

        /**
        * 允许线程空闲时间（单位：默认为秒）
        */
        private int keepAliveTime = 10;

        /**
        * 缓冲队列数
        */
        private int queueCapacity = 10;

        /**
        * 线程池名前缀
        */
        private String threadNamePrefix = "Jymj-Async-";

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Bean(name = "taskExecutor")
        public ThreadPoolTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(corePoolSize);
            executor.setMaxPoolSize(maxPoolSize);
            executor.setKeepAliveSeconds(keepAliveTime);
            executor.setQueueCapacity(queueCapacity);
            executor.setThreadNamePrefix(threadNamePrefix);
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
        }
}
