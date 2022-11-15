package com.jymj.mall.order.repository;

import com.jymj.mall.order.entity.MallOrderMdseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单商品信息
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-14
 */
@Repository
public interface MallOrderMdseDetailsRepository extends JpaRepository<MallOrderMdseDetails,Long> {
    List<MallOrderMdseDetails> findAllByOrderId(Long orderId);

    List<MallOrderMdseDetails> findAllByShopIdIn(List<Long> shopIdList);

    List<MallOrderMdseDetails> findAllByMdseNameIsLike(String generateSqlLike);
}
