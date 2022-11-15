package com.jymj.mall.order.repository;

import com.jymj.mall.order.entity.MallOrderDeliveryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单配送信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-17
 */
@Repository
public interface MallOrderDeliveryDetailsRepository extends JpaRepository<MallOrderDeliveryDetails,Long> {
    List<MallOrderDeliveryDetails> findAllByAddresseeIsLike(String generateSqlLike);

    List<MallOrderDeliveryDetails> findAllByMobileLike(String generateSqlLike);
}
