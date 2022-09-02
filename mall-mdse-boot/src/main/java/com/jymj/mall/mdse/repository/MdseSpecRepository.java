package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 库存规格
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Repository
public interface MdseSpecRepository extends JpaRepository<MdseSpec,Long> {
}
