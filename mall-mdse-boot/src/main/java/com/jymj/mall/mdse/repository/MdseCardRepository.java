package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 卡
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-07
 */
@Repository
public interface MdseCardRepository extends JpaRepository<MdseCard,Long>, JpaSpecificationExecutor<MdseCard> {
}
