package com.jymj.mall.common.web.service;

import java.util.List;
import java.util.Optional;

/**
 * 基础service
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
public interface BaseService<T, V, D> {

    T add(D dto);

    Optional<T> update(D dto);

    void delete(String ids);

    Optional<T> findById(Long id);

    V entity2vo(T entity);

    List<V> list2vo(List<T> entityList);
}
