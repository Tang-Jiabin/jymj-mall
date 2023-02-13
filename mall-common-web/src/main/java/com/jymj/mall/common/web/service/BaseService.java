package com.jymj.mall.common.web.service;

import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * 基础service
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
public interface BaseService<T, V, D> {

    /**
     * 添加
     *
     * @param dto 添加参数
     * @return 数据库实体
     */
    T add(D dto);

    /**
     * 修改
     *
     * @param dto 修改dto
     * @return 数据库实体
     */
    Optional<T> update(D dto);

    /**
     * 删除
     *
     * @param ids id集合 字符串类型 英文,分割
     */
    void delete(String ids);

    /**
     * 根据id查询
     *
     * @param id id
     * @return 实体
     */
    Optional<T> findById(Long id);

    /**
     * 实体转vo
     *
     * @param entity 实体
     * @return vo
     */
    V entity2vo(T entity);

    /**
     * list实体转vo
     *
     * @param entityList 实体列表
     * @return vo列表
     */
    default List<V> list2vo(List<T> entityList) {
        List<CompletableFuture<V>> futureList = Optional.of(entityList)
                .orElse(org.assertj.core.util.Lists.newArrayList())
                .stream().filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity))).collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
