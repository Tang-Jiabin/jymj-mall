package com.jymj.mall.shop.repository;


import com.jymj.mall.shop.entity.MallDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺详情
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Repository
public interface MallDetailsRepository extends JpaRepository<MallDetails,Long>, JpaSpecificationExecutor<MallDetails> {

}
