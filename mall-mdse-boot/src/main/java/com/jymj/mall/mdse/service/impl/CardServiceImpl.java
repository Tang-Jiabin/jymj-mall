package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.redis.utils.RedisUtils;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.dto.*;
import com.jymj.mall.mdse.entity.*;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.CardMdseRepository;
import com.jymj.mall.mdse.repository.MdseCardRepository;
import com.jymj.mall.mdse.repository.MdseCardRulesRepository;
import com.jymj.mall.mdse.service.CardService;
import com.jymj.mall.mdse.service.MdseService;
import com.jymj.mall.mdse.service.PictureService;
import com.jymj.mall.mdse.vo.CardInfo;
import com.jymj.mall.mdse.vo.EffectiveRulesInfo;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.mdse.vo.StockInfo;
import com.jymj.mall.search.api.MdseSearchFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 卡
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final MdseCardRepository cardRepository;
    private final PictureService pictureService;
    private final CardMdseRepository cardMdseRepository;
    private final MdseCardRulesRepository cardRulesRepository;
    private final MdseSearchFeignClient mdseSearchFeignClient;
    private final MdseService mdseService;
    private final RedisUtils redisUtils;
    private final ThreadPoolTaskExecutor executor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MdseCard add(CardDTO dto) {
        MdseCard card = new MdseCard();

        card.setName(dto.getName());
        card.setPrice(dto.getPrice());
        card.setInventoryQuantity(dto.getInventoryQuantity());
        card.setStartingQuantity(dto.getStartingQuantity());
        card.setRemainingQuantity(dto.getInventoryQuantity());
        card.setSalesVolume(0);
        card.setShowRemainingQuantity(dto.getShowRemainingQuantity());
        card.setRefund(dto.getRefund());
        card.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        card.setButtonName(dto.getButtonName());
        card.setDetails(dto.getDetails());
        card.setTypeId(dto.getTypeId());
        card.setStatus(dto.getStatus());
        card.setMallId(dto.getMallId());
        card.setDeleted(SystemConstants.DELETED_NO);

        EffectiveRulesDTO effectiveRules = dto.getEffectiveRules();
        MdseCardRules cardRules = new MdseCardRules();
        cardRules.setEffectiveRules(effectiveRules.getEffectiveRules());
        cardRules.setHoursLater(effectiveRules.getHoursLater());
        cardRules.setUsageRule(effectiveRules.getUsageRule());
        cardRules.setDays(effectiveRules.getDays());
        cardRules.setStartDate(effectiveRules.getStartDate());
        cardRules.setEndDate(effectiveRules.getEndDate());
        cardRules.setDeleted(SystemConstants.DELETED_NO);
        cardRules = cardRulesRepository.save(cardRules);

        card.setRulesId(cardRules.getRulesId());
        card = cardRepository.save(card);

        //商品图片
        List<PictureDTO> pictureList = dto.getPictureList();
        for (PictureDTO pictureDTO : pictureList) {
            PictureDTO picture = new PictureDTO();
            picture.setUrl(pictureDTO.getUrl());
            picture.setType(PictureType.CARD_PIC);
            picture.setCardId(card.getCardId());
            pictureService.add(picture);
        }

        Set<CardMdseDTO> cardMdseList = dto.getCardMdseList();
        for (CardMdseDTO cardMdseDTO : cardMdseList) {
            CardMdse cardMdse = new CardMdse();
            cardMdse.setCardId(card.getCardId());
            cardMdse.setMdseId(cardMdseDTO.getMdseId());
            cardMdse.setStockId(cardMdseDTO.getStockId());
            cardMdse.setDeleted(SystemConstants.DELETED_NO);
            cardMdseRepository.save(cardMdse);
        }

        return card;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<MdseCard> update(CardDTO dto) {

        Assert.notNull(dto.getCardId(), "卡id不能为空");

        Long cardId = dto.getCardId();

        Optional<MdseCard> mdseCardOptional = cardRepository.findById(cardId);
        if (mdseCardOptional.isPresent()) {
            MdseCard mdseCard = mdseCardOptional.get();

            mdseCard = updateBasicParams(dto, mdseCard);

            pictureService.updateCardPicture(dto.getPictureList(), dto.getCardId(), PictureType.CARD_PIC);

            updateCardMdse(dto);

            updateCardRules(dto, mdseCard);

            return Optional.of(mdseCard);

        }


        return Optional.empty();
    }


    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseCard> mdseList = cardRepository.findAllById(idList);
            cardRepository.deleteAll(mdseList);
        }
    }

    @Override
    public Optional<MdseCard> findById(Long id) {
        return cardRepository.findById(id);
    }

    @Override
    public CardInfo entity2vo(MdseCard entity) {

        if (entity != null) {
            String key = String.format("mall-mdse:cardInfo:id:%d", entity.getCardId());
            CardInfo value = (CardInfo) redisUtils.get(key);
            if (!ObjectUtils.isEmpty(value)) {
                executor.execute(()->syncUpdateVo(key, entity));
                return value;
            }
            CardInfo info = updateVo(entity);

            redisUtils.set(key, info, 60 * 60 * 8L);

        }
        return null;
    }

    @Async
    public void syncUpdateVo(String key, MdseCard entity) {
        CardInfo cardInfo = updateVo(entity);
        log.info("同步更新CardInfo : {}", cardInfo);
        redisUtils.set(key, cardInfo, 60 * 60 * 8L);
    }

    @NotNull
    private CardInfo updateVo(MdseCard entity) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setCardId(entity.getCardId());
        cardInfo.setName(entity.getName());
        cardInfo.setPrice(entity.getPrice());
        cardInfo.setInventoryQuantity(entity.getInventoryQuantity());
        cardInfo.setStartingQuantity(entity.getStartingQuantity());
        cardInfo.setShowRemainingQuantity(entity.getShowRemainingQuantity());
        cardInfo.setRefund(entity.getRefund());
        cardInfo.setInventoryReductionMethod(entity.getInventoryReductionMethod());
        cardInfo.setButtonName(entity.getName());
        cardInfo.setDetails(entity.getDetails());
        cardInfo.setTypeId(entity.getTypeId());
        cardInfo.setStockIdList(Sets.newHashSet());
        cardInfo.setStatus(entity.getStatus());
        cardInfo.setMallId(entity.getMallId());

        List<MallPicture> pictureList = pictureService.findAllByCardId(cardInfo.getCardId());
        cardInfo.setPictureList(pictureService.list2vo(pictureList));
        Optional<MdseCardRules> rulesOptional = cardRulesRepository.findById(entity.getRulesId());
        rulesOptional.ifPresent(rule -> cardInfo.setEffectiveRules(rule2vo(rule)));

        List<CardMdse> cardMdseList = cardMdseRepository.findAllByCardId(entity.getCardId());
        List<MallMdse> mdseList = mdseService.findAllById(cardMdseList.stream().map(CardMdse::getMdseId).collect(Collectors.toList()));
        List<MdseInfo> mdseInfoList = mdseService.list2vo(mdseList);
        mdseInfoList = mdseInfoList.stream().map(mdseInfo -> {
            List<StockInfo> stockList = mdseInfo.getStockList();
            List<StockInfo> stockInfoList = stockList.stream().filter(stockInfo -> cardMdseList.stream().map(CardMdse::getStockId).collect(Collectors.toList()).contains(stockInfo.getStockId())).collect(Collectors.toList());
            mdseInfo.setStockList(stockInfoList);
            return mdseInfo;
        }).collect(Collectors.toList());
        cardInfo.setMdseInfoList(mdseInfoList);
        return cardInfo;
    }


    @Override
    public List<CardInfo> list2vo(List<MdseCard> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MdseCard> findPage(CardPageQuery cardPageQuery) {

        Pageable pageable = PageUtils.getPageable(cardPageQuery);

        Specification<MdseCard> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (!ObjectUtils.isEmpty(cardPageQuery.getMallId())) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Long.class), cardPageQuery.getMallId()));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };
        return cardRepository.findAll(spec, pageable);
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticAddCardInfo(CardInfo cardInfo) {
        mdseSearchFeignClient.addCard(cardInfo);
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticDeleteCardInfo(String ids) {
        mdseSearchFeignClient.deleteCard(ids);
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticUpdateCardInfo(CardInfo cardInfo) {

        mdseSearchFeignClient.updateCard(cardInfo);
    }


    public EffectiveRulesInfo rule2vo(MdseCardRules cardRules) {
        EffectiveRulesInfo rulesInfo = new EffectiveRulesInfo();
        rulesInfo.setRulesId(cardRules.getRulesId());
        rulesInfo.setEffectiveRules(cardRules.getEffectiveRules());
        rulesInfo.setHoursLater(cardRules.getHoursLater());
        rulesInfo.setUsageRule(cardRules.getUsageRule());
        rulesInfo.setDays(cardRules.getDays());
        rulesInfo.setStartDate(cardRules.getStartDate());
        rulesInfo.setEndDate(cardRules.getEndDate());
        return rulesInfo;
    }

    private MdseCard updateBasicParams(CardDTO dto, MdseCard mdseCard) {

        boolean update = false;

        if (!ObjectUtils.nullSafeEquals(dto.getName(), mdseCard.getName())) {
            mdseCard.setName(dto.getName());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getPrice()) && !dto.getPrice().equals(mdseCard.getPrice())) {
            mdseCard.setPrice(dto.getPrice());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getStartingQuantity()) && !dto.getStartingQuantity().equals(mdseCard.getStartingQuantity())) {
            mdseCard.setStartingQuantity(dto.getStartingQuantity());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getInventoryQuantity()) && !dto.getInventoryQuantity().equals(mdseCard.getRemainingQuantity())) {
            mdseCard.setRemainingQuantity(dto.getInventoryQuantity());
            int inventoryQuantity = mdseCard.getRemainingQuantity() - dto.getInventoryQuantity() + mdseCard.getInventoryQuantity();
            mdseCard.setInventoryQuantity(inventoryQuantity);
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getShowRemainingQuantity()) && !dto.getShowRemainingQuantity().equals(mdseCard.getShowRemainingQuantity())) {
            mdseCard.setShowRemainingQuantity(dto.getShowRemainingQuantity());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getRefund()) && !dto.getRefund().equals(mdseCard.getRefund())) {
            mdseCard.setRefund(dto.getRefund());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getInventoryReductionMethod()) && !dto.getInventoryReductionMethod().equals(mdseCard.getInventoryReductionMethod())) {
            mdseCard.setInventoryReductionMethod(dto.getInventoryReductionMethod());
            update = true;
        }

        if (StringUtils.hasText(dto.getButtonName()) && !dto.getButtonName().equals(mdseCard.getButtonName())) {
            mdseCard.setButtonName(dto.getButtonName());
            update = true;
        }

        if (StringUtils.hasText(dto.getDetails()) && !dto.getDetails().equals(mdseCard.getDetails())) {
            mdseCard.setDetails(dto.getDetails());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getTypeId()) && !dto.getTypeId().equals(mdseCard.getTypeId())) {
            mdseCard.setTypeId(dto.getTypeId());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getStatus()) && !dto.getStatus().equals(mdseCard.getStatus())) {
            mdseCard.setStatus(dto.getStatus());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getMallId()) && !dto.getMallId().equals(mdseCard.getMallId())) {
            mdseCard.setMallId(dto.getMallId());
            update = true;
        }

        if (update) {
            mdseCard = cardRepository.save(mdseCard);
        }
        return mdseCard;
    }

    private void updateCardRules(CardDTO dto, MdseCard mdseCard) {
        EffectiveRulesDTO effectiveRules = dto.getEffectiveRules();
        Optional<MdseCardRules> rulesOptional = cardRulesRepository.findById(mdseCard.getRulesId());
        rulesOptional.ifPresent(rule -> {
            boolean update = false;
            if (!ObjectUtils.isEmpty(effectiveRules.getEffectiveRules()) && !effectiveRules.getEffectiveRules().equals(rule.getEffectiveRules())) {
                rule.setEffectiveRules(effectiveRules.getEffectiveRules());
                update = true;
            }
            if (!ObjectUtils.isEmpty(effectiveRules.getHoursLater()) && !effectiveRules.getHoursLater().equals(rule.getHoursLater())) {
                rule.setHoursLater(effectiveRules.getHoursLater());
                update = true;
            }
            if (!ObjectUtils.isEmpty(effectiveRules.getUsageRule()) && !effectiveRules.getUsageRule().equals(rule.getUsageRule())) {
                rule.setUsageRule(effectiveRules.getUsageRule());
                update = true;
            }
            if (!ObjectUtils.isEmpty(effectiveRules.getDays()) && !effectiveRules.getDays().equals(rule.getDays())) {
                rule.setDays(effectiveRules.getDays());
                update = true;
            }
            if (!ObjectUtils.isEmpty(effectiveRules.getStartDate()) && !effectiveRules.getStartDate().equals(rule.getStartDate())) {
                rule.setStartDate(effectiveRules.getStartDate());
                update = true;
            }
            if (!ObjectUtils.isEmpty(effectiveRules.getEndDate()) && !effectiveRules.getEndDate().equals(rule.getEndDate())) {
                rule.setEndDate(effectiveRules.getEndDate());
                update = true;
            }
            if (update) {
                cardRulesRepository.save(rule);
            }
        });
    }

    private void updateCardMdse(CardDTO dto) {
        Set<CardMdseDTO> dtoCardMdseList = dto.getCardMdseList();
        List<CardMdse> dbCardMdseList = cardMdseRepository.findAllByCardId(dto.getCardId());

        List<CardMdse> deleteCardMdseList = dbCardMdseList.stream()
                .filter(dbCardMdse -> {
                    for (CardMdseDTO dtoCardMdse : dtoCardMdseList) {
                        if (dtoCardMdse.getMdseId().equals(dbCardMdse.getMdseId()) && dtoCardMdse.getStockId().equals(dbCardMdse.getStockId())) {
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
        cardMdseRepository.deleteAll(deleteCardMdseList);

        List<CardMdse> addCardMdseList = dtoCardMdseList.stream()
                .filter(dtoCardMdse -> {
                    for (CardMdse cardMdse : dbCardMdseList) {
                        if (cardMdse.getMdseId().equals(dtoCardMdse.getMdseId()) && cardMdse.getStockId().equals(dtoCardMdse.getStockId())) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(dtoCardMdse ->
                        CardMdse.builder()
                                .cardId(dto.getCardId())
                                .mdseId(dtoCardMdse.getMdseId())
                                .stockId(dtoCardMdse.getStockId())
                                .build())
                .collect(Collectors.toList());
        cardMdseRepository.saveAll(addCardMdseList);
    }

}
