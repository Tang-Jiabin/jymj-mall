package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 商品库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Repository
public interface MdseStockRepository extends JpaRepository<MdseStock,Long> {
}
