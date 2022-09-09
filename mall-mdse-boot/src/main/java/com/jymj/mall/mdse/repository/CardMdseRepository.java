package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.CardMdse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 卡商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Repository
public interface CardMdseRepository extends JpaRepository<CardMdse, Long> {
    List<CardMdse> findAllByCardId(Long cardId);
}
