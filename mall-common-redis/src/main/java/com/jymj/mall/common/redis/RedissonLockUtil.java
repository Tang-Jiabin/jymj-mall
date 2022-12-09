package com.jymj.mall.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-25
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockUtil {

    private final RedissonClient redissonClient;

    /**
     * 加锁
     * @param lockname 名称
     */
    public Boolean lock(String lockName){
        try {
            if(null == redissonClient){
                log.error("RedissonClient is null");
                return false;
            }

            RLock lock = redissonClient.getLock(lockName);
            log.info("Thread [{}] lock [{}] success",Thread.currentThread().getName(),lockName);
            return lock.tryLock(60,30,TimeUnit.SECONDS);

        } catch (Exception e) {
            log.info("lock [{}] exception",lockName,e);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockname 名称
     */
    public void unlock(String lockName){
        try {
            if(null == redissonClient){
                log.error("RedissonClient is null");
                return;
            }
            RLock lock = redissonClient.getLock(lockName);
            lock.unlock();
            log.info("Thread [{}] unlock [{}] success",Thread.currentThread().getName(),lockName);
        } catch (Exception e) {
            log.info("unlock [{}] exception",lockName,e);
        }
    }

}
