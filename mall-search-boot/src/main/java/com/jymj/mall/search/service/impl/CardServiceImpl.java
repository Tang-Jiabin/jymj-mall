package com.jymj.mall.search.service.impl;

import com.jymj.mall.mdse.dto.CardPageQuery;
import com.jymj.mall.mdse.vo.CardInfo;
import com.jymj.mall.search.service.CardService;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 卡
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-21
 */
@Service
public class CardServiceImpl implements CardService {
    @Override
    public CardInfo add(CardInfo cardInfo) {
        return null;
    }

    @Override
    public void delete(String ids) {

    }

    @Override
    public CardInfo update(CardInfo cardInfo) {
        return null;
    }

    @Override
    public Optional<CardInfo> findById(Long cardId) {
        return Optional.empty();
    }

    @Override
    public SearchPage<CardInfo> findPage(CardPageQuery cardPageQuery) {
        return null;
    }
}
