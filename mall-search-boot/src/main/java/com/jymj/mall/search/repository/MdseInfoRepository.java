package com.jymj.mall.search.repository;

import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * 商品信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-19
 */
@Repository
public interface MdseInfoRepository extends ElasticsearchRepository<MdseInfo,Long> {
}
