package com.jymj.mall.pay.repository;

import com.jymj.mall.pay.entity.MallWeChatPayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-20
 */
@Repository
public interface MallWeChatPayInfoRepository extends JpaRepository<MallWeChatPayInfo,Long> {
}
