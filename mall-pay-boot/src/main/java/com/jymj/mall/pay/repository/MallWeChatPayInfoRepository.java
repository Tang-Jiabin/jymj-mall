package com.jymj.mall.pay.repository;

import com.jymj.mall.pay.entity.MallPayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-20
 */
@Repository
public interface MallWeChatPayInfoRepository extends JpaRepository<MallPayInfo, Long> {

    Long countByUserId(Long userId);

    Long countTotalFeeByUserId(Long userId);


    @Query(nativeQuery = true, value = "select sum(total_fee) from mall_pay_info where user_id = ?1")
    Long sumTotalFeeByUserId(Long userId);

    Optional<MallPayInfo> findFirstByUserIdOrderByCreateTimeDesc(Long userId);
}
