package com.jymj.mall.search.service;

import com.jymj.mall.mdse.dto.CardPageQuery;
import com.jymj.mall.mdse.vo.CardInfo;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.util.Optional;

/**
 * 卡
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-20
 */
public interface CardService {
    CardInfo add(CardInfo cardInfo);

    void delete(String ids);

    CardInfo update(CardInfo cardInfo);

    Optional<CardInfo> findById(Long cardId);

    SearchPage<CardInfo> findPage(CardPageQuery cardPageQuery);
}
