package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseCardRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 卡
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Repository
public interface MdseCardRulesRepository extends JpaRepository<MdseCardRules, Long> {
    Optional<MdseCardRules> findByCardId(Long mdseId);
}
