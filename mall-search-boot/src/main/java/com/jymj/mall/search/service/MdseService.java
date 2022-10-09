package com.jymj.mall.search.service;

import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.util.List;
import java.util.Optional;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-19
 */
public interface MdseService  {
    MdseInfo add(MdseInfo mdseInfo);

    void delete(String ids);

    MdseInfo update(MdseInfo mdseInfo);

    Optional<MdseInfo> findById(Long mdseId);

    SearchPage<MdseInfo> findPage(MdsePageQuery mdsePageQuery);

    List<MdseInfo> updateAll(List<MdseInfo> mdseInfoList);
}
