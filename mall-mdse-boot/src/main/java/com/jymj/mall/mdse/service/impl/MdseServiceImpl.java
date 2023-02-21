package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jymj.mall.common.constants.MdseConstants;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.dto.*;
import com.jymj.mall.mdse.entity.*;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.CardMdseRepository;
import com.jymj.mall.mdse.repository.MdseCardRulesRepository;
import com.jymj.mall.mdse.repository.MdsePurchaseRecordRepository;
import com.jymj.mall.mdse.repository.MdseRepository;
import com.jymj.mall.mdse.service.*;
import com.jymj.mall.mdse.vo.*;
import com.jymj.mall.search.api.MdseSearchFeignClient;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.MallInfo;
import com.jymj.mall.shop.vo.ShopInfo;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.vo.UserInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MdseServiceImpl implements MdseService {

    private final MdseRepository mdseRepository;
    private final GroupService groupService;
    private final BrandService brandService;
    private final MfgService mfgService;
    private final StockService stockService;
    private final PictureService pictureService;
    private final TypeService typeService;
    private final LabelService labelService;
    private final ShopFeignClient shopFeignClient;
    private final MallFeignClient mallFeignClient;
    private final MdseSearchFeignClient mdseSearchFeignClient;
    private final ThreadPoolTaskExecutor executor;
    private final MdseCardRulesRepository cardRulesRepository;
    private final CardMdseRepository cardMdseRepository;
    private final UserFeignClient userFeignClient;
    private final MdsePurchaseRecordRepository purchaseRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallMdse add(MdseDTO dto) {

        Assert.notNull(dto.getMallId(), "商城id不能为空");
        MallInfo mallInfo = null;
        Result<MallInfo> mallInfoResult = mallFeignClient.getMallById(dto.getMallId());
        if (Result.isSuccess(mallInfoResult)) {
            mallInfo = mallInfoResult.getData();
        } else {
            throw new BusinessException("商城不存在");
        }
        if (ObjectUtils.isEmpty(mallInfo)) {
            throw new BusinessException("商城不存在");
        }

        String code = mallInfo.getDistrictInfo().getCode();

        Long maxSequenceByMallId = mdseRepository.findMaxSequenceByMallId(dto.getMallId());
        if (ObjectUtils.isEmpty(maxSequenceByMallId)) {
            maxSequenceByMallId = 0L;
        }
        maxSequenceByMallId++;
        String sequence = String.format("%06d", maxSequenceByMallId);
        code = code + sequence;

        MallMdse mdse = new MallMdse();
        mdse.setSequence(maxSequenceByMallId);
        mdse.setName(dto.getName());
        mdse.setNumber(code);
        mdse.setPrice(dto.getPrice());
        mdse.setPostage(dto.getPostage());
        mdse.setStartingQuantity(dto.getStartingQuantity());
        mdse.setShowRemainingQuantity(Objects.nonNull(dto.getShowRemainingQuantity()) ? dto.getShowRemainingQuantity() : false);
        mdse.setRefund(dto.getRefund());
        mdse.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        mdse.setButtonName(dto.getButtonName());
        mdse.setDetails(dto.getDetails());
        mdse.setSalesVolume(0);
        mdse.setMallId(dto.getMallId());
        mdse.setDeleted(SystemConstants.DELETED_NO);
        mdse.setStatus(dto.getStatus() == null ? 2 : dto.getStatus());
        mdse.setClassify(MdseConstants.MDSE_TYPE_MDSE);
        mdse.setDeliveryMethod(dto.getDeliveryMethod());
        if (Objects.nonNull(dto.getClassify())) {
            mdse.setClassify(dto.getClassify());
        }
        //商品品牌
        Long brandId = dto.getBrandId();
        if (!ObjectUtils.isEmpty(brandId)) {
            Optional<MdseBrand> brandOptional = brandService.findById(brandId);
            MdseBrand brand = brandOptional.orElseThrow(() -> new BusinessException("品牌不存在"));
            mdse.setBrandId(brand.getBrandId());
        }

        //商品厂家
        Long mfgId = dto.getMfgId();
        if (!ObjectUtils.isEmpty(mfgId)) {
            Optional<MdseMfg> mfgOptional = mfgService.findById(mfgId);
            MdseMfg mfg = mfgOptional.orElseThrow(() -> new BusinessException("厂家不存在"));
            mdse.setMfgId(mfg.getMfgId());
        }

        //商品店铺
        Long shopId = dto.getShopId();
        if (Objects.nonNull(shopId)) {
            Result<ShopInfo> shopInfoResult = shopFeignClient.getShopById(shopId);
            if (!Result.isSuccess(shopInfoResult)) {
                throw new BusinessException("店铺不存在");
            }
            mdse.setShopId(shopId);
        }

        //商品类型
        Long typeId = dto.getTypeId();
        if (!ObjectUtils.isEmpty(typeId)) {
            Optional<MdseType> typeOptional = typeService.findById(typeId);
            MdseType mdseType = typeOptional.orElseThrow(() -> new BusinessException("类型不存在"));
            mdse.setTypeId(mdseType.getTypeId());
        }

        mdse = mdseRepository.save(mdse);
        Long mdseId = mdse.getMdseId();

        //商品图片
        List<PictureDTO> pictureList = dto.getPictureList();
        for (PictureDTO pictureDTO : pictureList) {
            PictureDTO picture = new PictureDTO();
            picture.setUrl(pictureDTO.getUrl());
            picture.setType(PictureType.MDSE_PIC);
            picture.setMdseId(mdseId);
            pictureService.add(picture);
        }

        //商品视频
        List<PictureDTO> videoList = dto.getVideoList();
        if (!CollectionUtils.isEmpty(videoList)) {
            for (PictureDTO pictureDTO : videoList) {
                PictureDTO picture = new PictureDTO();
                picture.setUrl(pictureDTO.getUrl());
                picture.setType(PictureType.MDSE_VIDEO);
                picture.setMdseId(mdseId);
                pictureService.add(picture);
            }
        }

        //商品标签
        Set<Long> labelIdList = dto.getLabelIdList();
        if (!CollectionUtils.isEmpty(labelIdList)) {
            List<MdseLabel> labelList = labelService.findAllById(Lists.newArrayList(labelIdList));
            labelService.addMdseLabelMap(mdseId, labelList.stream().map(MdseLabel::getLabelId).collect(Collectors.toList()));
        }

        //商品分组
        Set<Long> groupIdList = dto.getGroupIdList();
        if (!CollectionUtils.isEmpty(groupIdList)) {
            List<MdseGroup> groupList = groupService.findAllById(Lists.newArrayList(groupIdList));
            groupService.addMdseGroupMap(mdseId, groupList.stream().map(MdseGroup::getGroupId).collect(Collectors.toList()));
        }

        //商品库存
        Set<StockDTO> stockList = dto.getStockList();
        Optional.of(stockList)
                .orElse(Sets.newHashSet())
                .stream()
                .filter(stock -> !ObjectUtils.isEmpty(stock))
                .forEach(stockDTO -> {
                    stockDTO.setMdseId(mdseId);
                    stockService.add(stockDTO);
                });

        //卡商品列表
        if (!CollectionUtils.isEmpty(dto.getCardMdseList())) {
            Set<CardMdseDTO> cardMdseDTOList = dto.getCardMdseList();
            List<CardMdse> cardMdseList = Lists.newArrayList();
            for (CardMdseDTO cardMdseDTO : cardMdseDTOList) {
                CardMdse cardMdse = new CardMdse();
                cardMdse.setCardId(mdseId);
                cardMdse.setMdseId(cardMdseDTO.getMdseId());
                cardMdse.setStockId(cardMdseDTO.getStockId());
                cardMdse.setQuantity(Objects.nonNull(cardMdseDTO.getQuantity()) ? cardMdseDTO.getQuantity() : 1);
                cardMdse.setDeleted(SystemConstants.DELETED_NO);
                cardMdseList.add(cardMdse);
            }
            cardMdseRepository.saveAll(cardMdseList);
        }
        //使用规则
        if (Objects.nonNull(dto.getEffectiveRules())) {
            EffectiveRulesDTO effectiveRules = dto.getEffectiveRules();
            MdseCardRules cardRules = new MdseCardRules();
            cardRules.setCardId(mdseId);
            cardRules.setEffectiveRules(effectiveRules.getEffectiveRules());
            cardRules.setHoursLater(effectiveRules.getHoursLater());
            cardRules.setUsageRule(effectiveRules.getUsageRule());
            cardRules.setDays(effectiveRules.getDays());
            cardRules.setStartDate(effectiveRules.getStartDate());
            cardRules.setEndDate(effectiveRules.getEndDate());
            cardRules.setDeleted(SystemConstants.DELETED_NO);
            cardRulesRepository.save(cardRules);
        }

        return mdse;
    }

    @Override
    @GlobalTransactional(name = "mall-mdse-mdse-update", rollbackFor = Exception.class)
    public Optional<MallMdse> update(MdseDTO dto) {

        Assert.notNull(dto.getMdseId(), "商品id不能为空");

        Optional<MallMdse> mdseOptional = findById(dto.getMdseId());

        MallMdse mallMdse = mdseOptional.orElseThrow(() -> new BusinessException("商品不存在"));

        //商品名称
        if (StringUtils.hasText(dto.getName())) {
            mallMdse.setName(dto.getName());
        }
        //商品编号
        if (StringUtils.hasText(dto.getNumber())) {
            mallMdse.setNumber(dto.getNumber());
        }
        //商品参考价格
        if (!ObjectUtils.isEmpty(dto.getPrice())) {
            mallMdse.setPrice(dto.getPrice());
        }
        //商品运费价格
        if (!ObjectUtils.isEmpty(dto.getPostage())) {
            mallMdse.setPostage(dto.getPostage());
        }
        //商品起售数量
        if (!ObjectUtils.isEmpty(dto.getStartingQuantity())) {
            mallMdse.setStartingQuantity(dto.getStartingQuantity());
        }
        //是否显示剩余数量
        if (!ObjectUtils.isEmpty(dto.getShowRemainingQuantity())) {
            mallMdse.setShowRemainingQuantity(dto.getShowRemainingQuantity());
        }
        //销售数量
        if (!ObjectUtils.isEmpty(dto.getSalesVolume())) {
            mallMdse.setSalesVolume(ObjectUtils.isEmpty(mallMdse.getSalesVolume()) ? dto.getSalesVolume() : mallMdse.getSalesVolume() + dto.getSalesVolume());
        }
        //商品是否允许退款
        if (!ObjectUtils.isEmpty(dto.getRefund())) {
            mallMdse.setRefund(dto.getRefund());
        }
        //商品库存减少方式
        if (!ObjectUtils.isEmpty(dto.getInventoryReductionMethod())) {
            mallMdse.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        }
        //商品立即购买按钮名称
        if (StringUtils.hasText(dto.getButtonName())) {
            mallMdse.setButtonName(dto.getButtonName());
        }
        //商品详情
        if (StringUtils.hasText(dto.getDetails())) {
            mallMdse.setDetails(dto.getDetails());
        }
        //商品品牌id
        if (!ObjectUtils.isEmpty(dto.getBrandId())) {
            mallMdse.setBrandId(dto.getBrandId());
        }
        //商品厂家id
        if (!ObjectUtils.isEmpty(dto.getMfgId())) {
            mallMdse.setMfgId(dto.getMfgId());
        }
        //商品厂家id
        if (!ObjectUtils.isEmpty(dto.getShopId())) {
            mallMdse.setShopId(dto.getShopId());
        }
        //商品类型id
        if (!ObjectUtils.isEmpty(dto.getTypeId())) {
            mallMdse.setTypeId(dto.getTypeId());
        }
        //商品状态
        if (!ObjectUtils.isEmpty(dto.getStatus())) {
            mallMdse.setStatus(dto.getStatus());
        }
        //卡生效规则
        if (Objects.nonNull(dto.getEffectiveRules())) {
            updateCardRules(dto, mallMdse);
        }
        //配送方式
        if (!ObjectUtils.isEmpty(dto.getDeliveryMethod())) {
            mallMdse.setDeliveryMethod(dto.getDeliveryMethod());
        }
        List<CompletableFuture<Void>> futureList = Lists.newArrayList();
        //卡商品信息
        if (!CollectionUtils.isEmpty(dto.getCardMdseList())) {
            CompletableFuture<Void> updateCardMdseFuture = CompletableFuture.runAsync(() -> updateCardMdseList(dto), executor).exceptionally(throwable -> {
                log.error("更新卡商品失败:{}", dto.getCardMdseList());
                throwable.printStackTrace();
                throw new BusinessException("更新卡商品失败");
            });
            futureList.add(updateCardMdseFuture);
        }

        if (!CollectionUtils.isEmpty(dto.getPictureList())) {
            CompletableFuture<Void> updatePictureFuture = CompletableFuture.runAsync(() -> pictureService.updateMdsePicture(dto.getPictureList(), dto.getMdseId(), PictureType.MDSE_PIC), executor).exceptionally(throwable -> {
                log.error("更新商品图片失败:{}", dto.getPictureList());
                throwable.printStackTrace();
                throw new BusinessException("更新商品图片失败");
            });
            futureList.add(updatePictureFuture);
        }
        if (!CollectionUtils.isEmpty(dto.getVideoList())) {
            CompletableFuture<Void> updateVideoFuture = CompletableFuture.runAsync(() -> pictureService.updateMdsePicture(dto.getVideoList(), dto.getMdseId(), PictureType.MDSE_VIDEO), executor).exceptionally(throwable -> {
                log.error("更新商品视频失败:{}", dto.getVideoList());
                throwable.printStackTrace();
                throw new BusinessException("更新商品视频失败");
            });
            futureList.add(updateVideoFuture);
        }

        if (!ObjectUtils.isEmpty(dto.getGroupIdList())) {
            CompletableFuture<Void> updateGroupFuture = CompletableFuture.runAsync(() -> updateMdseGroup(dto), executor).exceptionally(throwable -> {
                log.error("更新商品分组失败:{}", dto.getVideoList());
                throwable.printStackTrace();
                throw new BusinessException("更新商品分组失败");
            });
            futureList.add(updateGroupFuture);
        }

        if (!ObjectUtils.isEmpty(dto.getLabelIdList())) {
            CompletableFuture<Void> updateLabelFuture = CompletableFuture.runAsync(() -> updateMdseLabel(dto), executor).exceptionally(throwable -> {
                log.error("更新商品标签失败:{}", dto.getVideoList());
                throwable.printStackTrace();
                throw new BusinessException("更新商品标签失败");
            });
            futureList.add(updateLabelFuture);
        }

        if (!ObjectUtils.isEmpty(dto.getStockList())) {

                MdseDTO mdseDTO = new MdseDTO();
                mdseDTO.setMdseId(dto.getMdseId());
                mdseDTO.setStockList(dto.getStockList());
                updateMdseStock(mdseDTO);

//            CompletableFuture<Void> updateStockFuture = CompletableFuture.runAsync(() -> updateMdseStock(dto), executor).exceptionally(throwable -> {
//                log.error("更新商品规格失败:{}", dto.getVideoList());
//                throwable.printStackTrace();
//                throw new BusinessException("更新商品规格失败");
//            });
//            futureList.add(updateStockFuture);
        }

        if (!futureList.isEmpty()) {
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        }

        return Optional.of(mdseRepository.save(mallMdse));
    }


    private void updateCardMdseList(MdseDTO dto) {
        Set<CardMdseDTO> cardMdseDTOList = dto.getCardMdseList();
        List<CardMdse> allByCardId = cardMdseRepository.findAllByCardId(dto.getMdseId());
        cardMdseRepository.deleteAll(allByCardId);
        List<CardMdse> cardMdseList = Lists.newArrayList();
        for (CardMdseDTO cardMdseDTO : cardMdseDTOList) {
            CardMdse cardMdse = new CardMdse();
            cardMdse.setCardId(dto.getMdseId());
            cardMdse.setMdseId(cardMdseDTO.getMdseId());
            cardMdse.setStockId(cardMdseDTO.getStockId());
            cardMdse.setQuantity(cardMdseDTO.getQuantity());
            cardMdse.setDeleted(SystemConstants.DELETED_NO);
            cardMdseList.add(cardMdse);
        }
        cardMdseRepository.saveAll(cardMdseList);
    }

    @CacheEvict(value = "mall-mdse:card-rules-result:", key = "'card-id:'+#mallMdse.mdseId")
    public void updateCardRules(MdseDTO dto, MallMdse mallMdse) {
        EffectiveRulesDTO effectiveRules = dto.getEffectiveRules();
        Optional<MdseCardRules> cardRulesOptional = cardRulesRepository.findByCardId(mallMdse.getMdseId());
        MdseCardRules cardRules = new MdseCardRules();
        if (cardRulesOptional.isPresent()) {
            cardRules = cardRulesOptional.get();
        }
        cardRules.setCardId(mallMdse.getMdseId());
        cardRules.setEffectiveRules(effectiveRules.getEffectiveRules());
        cardRules.setHoursLater(effectiveRules.getHoursLater());
        cardRules.setUsageRule(effectiveRules.getUsageRule());
        cardRules.setDays(effectiveRules.getDays());
        cardRules.setStartDate(effectiveRules.getStartDate());
        cardRules.setEndDate(effectiveRules.getEndDate());
        cardRules.setDeleted(SystemConstants.DELETED_NO);
        cardRulesRepository.save(cardRules);
    }

    private void updateMdseStock(MdseDTO dto) {


        List<MdseStock> mdseStockList = stockService.findAllByMdseId(dto.getMdseId());
        //需要删除的库存
        List<MdseStock> deleteMdseStockList = mdseStockList.stream().filter(mdseStock -> !dto.getStockList().stream().map(StockDTO::getStockId).filter(Objects::nonNull).collect(Collectors.toList()).contains(mdseStock.getStockId())).collect(Collectors.toList());
        stockService.deleteMdseStock(deleteMdseStockList);

        //需要添加的库存
        List<StockDTO> addMdseStockList = dto.getStockList().stream().filter(stockDTO -> {
            if (stockDTO.getStockId() == null || stockDTO.getStockId() == 0L) {
                stockDTO.setMdseId(dto.getMdseId());
                return true;
            }
            for (MdseStock stock : mdseStockList) {
                if (stock.getStockId().equals(stockDTO.getStockId())) {
                    return false;
                }
            }
            return false;
        }).collect(Collectors.toList());
        addMdseStockList.forEach(stockService::add);


        //需要修改的库存
        List<StockDTO> updateMdseStockList = dto.getStockList().stream().filter(stockDTO -> mdseStockList.stream().map(MdseStock::getStockId).collect(Collectors.toList()).contains(stockDTO.getStockId())).collect(Collectors.toList());
        updateMdseStockList.forEach(stockService::update);

    }

    private void updateMdseLabel(MdseDTO dto) {

        List<MallMdseLabelMap> mdseLabelMapList = labelService.findMdseLabelAllByMdseId(dto.getMdseId());
        //需要删除的标签
        List<MallMdseLabelMap> deleteMdseLabelMapList = mdseLabelMapList.stream().filter(mdseLabel -> !dto.getLabelIdList().contains(mdseLabel.getLabelId())).collect(Collectors.toList());
        labelService.deleteMdseLabel(deleteMdseLabelMapList);
        //需要添加的标签
        List<Long> addMdseLabelIdList = dto.getLabelIdList().stream().filter(labelId -> !mdseLabelMapList.stream().map(MallMdseLabelMap::getLabelId).collect(Collectors.toList()).contains(labelId)).collect(Collectors.toList());
        labelService.addMdseLabelMap(dto.getMdseId(), addMdseLabelIdList);


    }

    private void updateMdseGroup(MdseDTO dto) {

        List<MallMdseGroupMap> mdseGroupList = groupService.findMdseGroupAllByMdseId(dto.getMdseId());
        //需要删除的分组
        List<MallMdseGroupMap> deleteMdseGroupList = mdseGroupList.stream().filter(mdseGroup -> !dto.getGroupIdList().contains(mdseGroup.getGroupId())).collect(Collectors.toList());
        groupService.deleteMdseGroupAll(deleteMdseGroupList);
        //需要添加的分组
        List<Long> addMdseGroupIdList = dto.getGroupIdList().stream().filter(id -> !mdseGroupList.stream().map(MallMdseGroupMap::getGroupId).collect(Collectors.toList()).contains(id)).collect(Collectors.toList());
        groupService.addMdseGroupMap(dto.getMdseId(), addMdseGroupIdList);


    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallMdse> mdseList = mdseRepository.findAllById(idList);
            mdseRepository.deleteAll(mdseList);
        }
    }

    @Override
    public Optional<MallMdse> findById(Long id) {
        return mdseRepository.findById(id);
    }

    @Override
    public MdseInfo entity2vo(MallMdse entity) {
        return entity2vo(entity, MdseInfoShow.builder()
                .group(SystemConstants.STATUS_OPEN)
                .stock(SystemConstants.STATUS_OPEN)
                .label(SystemConstants.STATUS_OPEN)
                .mfg(SystemConstants.STATUS_OPEN)
                .type(SystemConstants.STATUS_OPEN)
                .brand(SystemConstants.STATUS_OPEN)
                .shop(SystemConstants.STATUS_OPEN)
                .picture(SystemConstants.STATUS_OPEN)
                .build());
    }

    @Override
    public List<MdseInfo> list2vo(List<MallMdse> entityList) {
        List<CompletableFuture<MdseInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public MdseInfo entity2vo(MallMdse entity, MdseInfoShow show) {
        if (!ObjectUtils.isEmpty(entity)) {
            MdseInfo mdseInfo = getMdseInfo(entity);
            setMdseInfoOther(entity, show, mdseInfo);
            setCardInfo(entity, mdseInfo);
            return mdseInfo;
        }
        return null;
    }

    private MdseInfo setCardInfo(MallMdse entity, MdseInfo mdseInfo) {
        if (entity.getClassify().equals(MdseConstants.MDSE_TYPE_CARD)) {
            Optional<MdseCardRules> rulesOptional = cardRulesRepository.findByCardId(entity.getMdseId());
            rulesOptional.ifPresent(rule -> mdseInfo.setEffectiveRules(rule2vo(rule)));

            List<CardMdse> cardMdseList = cardMdseRepository.findAllByCardId(entity.getMdseId());
            List<MallMdse> mdseList = findAllById(cardMdseList.stream().map(CardMdse::getMdseId).collect(Collectors.toList()));
            List<MdseInfo> mdseInfoList = mdseList.stream().map(mdse -> entity2vo(mdse, MdseInfoShow.builder().stock(SystemConstants.STATUS_OPEN).shop(SystemConstants.STATUS_OPEN).picture(SystemConstants.STATUS_OPEN).build())).collect(Collectors.toList());
            mdseInfoList = mdseInfoList.stream().map(mdseInfo2 -> {
                List<StockInfo> stockList = mdseInfo2.getStockList();
                List<StockInfo> stockInfoList = stockList.stream().filter(stockInfo -> cardMdseList.stream().map(CardMdse::getStockId).collect(Collectors.toList()).contains(stockInfo.getStockId())).collect(Collectors.toList());
                cardMdseList.stream()
                        .filter(cardMdse -> cardMdse.getMdseId().equals(mdseInfo2.getMdseId()))
                        .findFirst()
                        .ifPresent(card -> mdseInfo2.setStartingQuantity(card.getQuantity()));

                mdseInfo2.setStockList(stockInfoList);
                return mdseInfo2;
            }).collect(Collectors.toList());
            mdseInfo.setMdseInfoList(mdseInfoList);
        }
        return mdseInfo;
    }

    @Override
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

    private MdseInfo setMdseInfoOther(MallMdse entity, MdseInfoShow show, MdseInfo mdseInfo) {
        if (SystemConstants.STATUS_OPEN.equals(show.getGroup())) {
            mdseInfo.setGroupList(findGroupListByMdseId(mdseInfo.getMdseId()));
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getStock())) {
            List<StockInfo> stockInfos = findStockListByMdseId(mdseInfo.getMdseId());

            mdseInfo.setStockList(stockInfos);

            List<SpecMap> specMapList = Lists.newArrayList();
            for (StockInfo stockInfo : stockInfos) {
                setSpecMap(specMapList, stockInfo.getSpecA());
                setSpecMap(specMapList, stockInfo.getSpecB());
                setSpecMap(specMapList, stockInfo.getSpecC());
            }
            mdseInfo.setSpecMap(specMapList);
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getLabel())) {
            mdseInfo.setLabelInfoList(findLabelListByMdseId(mdseInfo.getMdseId()));
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getPicture())) {
            List<PictureInfo> pictureInfoList = findPictureListByMdseId(mdseInfo.getMdseId());
            mdseInfo.setPictureList(pictureInfoList.stream().filter(pictureInfo -> pictureInfo.getType() == PictureType.MDSE_PIC).collect(Collectors.toList()));
            mdseInfo.setVideoList(pictureInfoList.stream().filter(pictureInfo -> pictureInfo.getType() == PictureType.MDSE_VIDEO).collect(Collectors.toList()));
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getMfg()) && !ObjectUtils.isEmpty(entity.getMfgId())) {
            Optional<MdseMfg> mfgOptional = mfgService.findById(entity.getMfgId());
            if (mfgOptional.isPresent()) {
                MdseMfg mdseMfg = mfgOptional.get();
                mdseInfo.setMfg(mfgService.entity2vo(mdseMfg));
            }

        }
        if (SystemConstants.STATUS_OPEN.equals(show.getType()) && !ObjectUtils.isEmpty(entity.getTypeId())) {
            Optional<MdseType> typeOptional = typeService.findById(entity.getTypeId());
            if (typeOptional.isPresent()) {
                MdseType mdseType = typeOptional.get();
                mdseInfo.setTypeInfo(typeService.entity2vo(mdseType));
            }
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getBrand()) && !ObjectUtils.isEmpty(entity.getBrandId())) {
            Optional<MdseBrand> brandOptional = brandService.findById(entity.getBrandId());
            if (brandOptional.isPresent()) {
                MdseBrand mdseBrand = brandOptional.get();
                mdseInfo.setBrand(brandService.entity2vo(mdseBrand));
            }
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getShop()) && !ObjectUtils.isEmpty(entity.getShopId())) {
            Result<ShopInfo> shopInfoResult = shopFeignClient.getShopById(entity.getShopId());
            if (Result.isSuccess(shopInfoResult)) {
                mdseInfo.setShopInfo(shopInfoResult.getData());
                mdseInfo.setLocation(new GeoPoint(shopInfoResult.getData().getLatitude(), shopInfoResult.getData().getLongitude()));
            }
        }
        return mdseInfo;
    }

    @NotNull
    private static MdseInfo getMdseInfo(MallMdse entity) {
        MdseInfo mdseInfo = new MdseInfo();
        mdseInfo.setMdseId(entity.getMdseId());
        mdseInfo.setName(entity.getName());
        mdseInfo.setNumber(entity.getNumber());
        mdseInfo.setPrice(entity.getPrice());
        mdseInfo.setPostage(entity.getPostage());
        mdseInfo.setStartingQuantity(entity.getStartingQuantity());
        mdseInfo.setShowRemainingQuantity(entity.isShowRemainingQuantity());
        mdseInfo.setRefund(entity.isRefund());
        mdseInfo.setInventoryReductionMethod(entity.getInventoryReductionMethod());
        mdseInfo.setButtonName(entity.getButtonName());
        mdseInfo.setDetails(entity.getDetails());
        mdseInfo.setSalesVolume(entity.getSalesVolume());
        mdseInfo.setCreateTime(DateFormatUtils.format(entity.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        mdseInfo.setStatus(entity.getStatus());
        mdseInfo.setMallId(entity.getMallId());
        mdseInfo.setClassify(entity.getClassify());
        mdseInfo.setDeliveryMethod(entity.getDeliveryMethod());
        mdseInfo.setLocation(new GeoPoint(0.0, 0.0));
        return mdseInfo;
    }

    @Override
    public List<MdseInfo> list2vo(List<MallMdse> entityList, MdseInfoShow show) {
        List<CompletableFuture<MdseInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity, show), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<MallMdse> findAllById(List<Long> idList) {

        return mdseRepository.findAllById(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(MdseStatusDTO mdseDTO) {
        List<Long> mdseIds = mdseDTO.getMdseIds();
        List<MallMdse> mallMdseList = mdseRepository.findAllById(mdseIds);
        if (!ObjectUtils.isEmpty(mdseDTO.getStatus())) {
            mallMdseList.forEach(mdse -> mdse.setStatus(mdseDTO.getStatus()));
            mdseRepository.saveAll(mallMdseList);
        }
        if (!ObjectUtils.isEmpty(mdseDTO.getGroupId())) {
            groupService.addMdseGroupMap(mallMdseList.stream().map(MallMdse::getMdseId).collect(Collectors.toList()), mdseDTO.getGroupId());
        }
    }

    @Override
    public Page<MallMdse> findPage(MdsePageQuery mdsePageQuery) {

        Pageable pageable = PageUtils.getPageable(mdsePageQuery);

        Specification<MallMdse> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            Set<Long> mdseIdSet = Sets.newHashSet();

            //商品分类
            if (Objects.nonNull(mdsePageQuery.getClassify())) {
                list.add(criteriaBuilder.equal(root.get("classify").as(Integer.class), mdsePageQuery.getClassify()));
            }

            //创建时间
            if (mdsePageQuery.getStartCreateDate() != null && mdsePageQuery.getEndCreateDate() != null) {
                list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), mdsePageQuery.getStartCreateDate(), mdsePageQuery.getEndCreateDate()));
            }

            //价格
            if (mdsePageQuery.getStartPrice() != null && mdsePageQuery.getEndPrice() != null) {
                list.add(criteriaBuilder.between(root.get("price").as(BigDecimal.class), mdsePageQuery.getStartPrice(), mdsePageQuery.getEndPrice()));
            }

            //编号
            if (StringUtils.hasText(mdsePageQuery.getNumber())) {
                list.add(criteriaBuilder.like(root.get("number").as(String.class), mdsePageQuery.getNumber() + SystemConstants.SQL_LIKE));
            }

            //名称
            if (StringUtils.hasText(mdsePageQuery.getName())) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), mdsePageQuery.getName() + SystemConstants.SQL_LIKE));
            }

            //商品状态
            if (mdsePageQuery.getStatus() != null) {
                list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), mdsePageQuery.getStatus()));
            }

            //品牌id
            if (mdsePageQuery.getBrandId() != null) {
                list.add(criteriaBuilder.equal(root.get("brandId").as(Long.class), mdsePageQuery.getBrandId()));
            }

            //厂家id
            if (mdsePageQuery.getMfgId() != null) {
                list.add(criteriaBuilder.equal(root.get("mfgId").as(Long.class), mdsePageQuery.getMfgId()));
            }

            //商场id
            if (mdsePageQuery.getMallId() != null) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Long.class), mdsePageQuery.getMallId()));
            }

            //店铺id
            if (mdsePageQuery.getShopId() != null) {
                list.add(criteriaBuilder.equal(root.get("shopId").as(Long.class), mdsePageQuery.getShopId()));
            }

            if (!ObjectUtils.isEmpty(mdsePageQuery.getUserId())) {
                List<MdsePurchaseRecord> purchaseRecordList = ObjectUtils.isEmpty(mdsePageQuery.getTypeId()) ? purchaseRecordRepository.findAllByUserId(mdsePageQuery.getUserId()) : purchaseRecordRepository.findAllByUserIdAndType(mdsePageQuery.getUserId(), mdsePageQuery.getTypeId());
                purchaseRecordList.forEach(purchaseRecord -> mdseIdSet.add(purchaseRecord.getMdseId()));
                mdseIdSet.add(0L);
            }


            //数量大于等于
            if (mdsePageQuery.getQuantityGreaterThanOrEqual() != null) {
                List<MdseStock> stockList = stockService.findAllByRemainingStockGreaterThanOrEqual(mdsePageQuery.getQuantityGreaterThanOrEqual());
                stockList.stream().map(MdseStock::getMdseId).filter(Objects::nonNull).forEach(mdseIdSet::add);
                mdseIdSet.add(0L);
            }

            //数量小于等于
            if (mdsePageQuery.getQuantityLessThanOrEqual() != null) {
                List<MdseStock> stockList = stockService.findAllByRemainingStockLessThanEqual(mdsePageQuery.getQuantityLessThanOrEqual());
                stockList.stream().map(MdseStock::getMdseId).filter(Objects::nonNull).forEach(mdseIdSet::add);
                mdseIdSet.add(0L);
            }

            //销量大于等于
            if (mdsePageQuery.getSalesVolumeGreaterThanOrEqual() != null) {
                list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salesVolume").as(Integer.class), mdsePageQuery.getSalesVolumeGreaterThanOrEqual()));
            }

            //销量小于等于
            if (mdsePageQuery.getSalesVolumeLessThanOrEqual() != null) {
                list.add(criteriaBuilder.lessThanOrEqualTo(root.get("salesVolume").as(Integer.class), mdsePageQuery.getSalesVolumeLessThanOrEqual()));
            }

            //分组
            if (!ObjectUtils.isEmpty(mdsePageQuery.getGroupId())) {
                List<MallMdseGroupMap> mdseGroupMaps = groupService.findAllMdseGroupById(mdsePageQuery.getGroupId());
                mdseGroupMaps.forEach(mdseGroup -> mdseIdSet.add(mdseGroup.getMdseId()));
                mdseIdSet.add(0L);
            }

            //标签
            if (!ObjectUtils.isEmpty(mdsePageQuery.getLabelId())) {
                List<MallMdseLabelMap> mdseLabelMaps = labelService.findMdseLabelAllByLabelId(mdsePageQuery.getLabelId());
                mdseLabelMaps.forEach(mdseLabel -> mdseIdSet.add(mdseLabel.getMdseId()));
                mdseIdSet.add(0L);
            }

            //类型
            if (!ObjectUtils.isEmpty(mdsePageQuery.getTypeId())) {
                list.add(criteriaBuilder.equal(root.get("typeId").as(Integer.class), mdsePageQuery.getTypeId()));
            }

            //库存减少方式
            if (!ObjectUtils.isEmpty(mdsePageQuery.getInventoryReductionMethod())) {
                list.add(criteriaBuilder.equal(root.get("inventoryReductionMethod").as(InventoryReductionMethod.class), mdsePageQuery.getInventoryReductionMethod()));
            }

            if (!CollectionUtils.isEmpty(mdseIdSet)) {
                CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
                mdseIdSet.forEach(mdseIdIn::value);
                list.add(mdseIdIn);
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };
        return mdseRepository.findAll(spec, pageable);
    }

    public List<PictureInfo> findPictureListByMdseId(Long mdseId) {
        if (!ObjectUtils.isEmpty(mdseId)) {
            List<MallPicture> pictureList = pictureService.findAllByMdseId(mdseId).stream().filter(pic -> pic.getType() != PictureType.STOCK_SPEC).collect(Collectors.toList());
            return pictureService.list2vo(pictureList);
        }
        return Collections.emptyList();
    }

    @Override
    public List<GroupInfo> findGroupListByMdseId(Long mdseId) {
        if (!ObjectUtils.isEmpty(mdseId)) {
            List<MdseGroup> groupList = groupService.findAllByMdseId(mdseId);
            return groupService.list2vo(groupList);
        }
        return Collections.emptyList();
    }

    public List<StockInfo> findStockListByMdseId(Long mdseId) {

        if (!ObjectUtils.isEmpty(mdseId)) {
            List<MdseStock> stockList = stockService.findAllByMdseId(mdseId);

            return stockService.list2vo(stockList);
        }
        return Collections.emptyList();
    }

    private static void setSpecMap(List<SpecMap> specMapList, SpecInfo specInfo) {
        if (specInfo != null) {
            SpecMap specMap = null;
            for (SpecMap spec : specMapList) {
                if (spec.getKey().equals(specInfo.getKey())) {
                    specMap = spec;
                }
            }
            if (specMap == null) {
                List<String> list = Lists.newArrayList();
                list.add(specInfo.getValue());
                specMapList.add(new SpecMap(specInfo.getKey(), list));
            } else {
                if (!specMap.getValues().contains(specInfo.getValue())) {
                    specMap.getValues().add(specInfo.getValue());
                }
            }
        }
    }

    public List<LabelInfo> findLabelListByMdseId(Long mdseId) {
        if (!ObjectUtils.isEmpty(mdseId)) {
            List<MdseLabel> labelList = labelService.findAllByMdseId(mdseId);
            return labelService.list2vo(labelList);
        }
        return Collections.emptyList();
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticAddMdseInfo(MdseInfo mdseInfo) {
        mdseSearchFeignClient.addMdse(mdseInfo);
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticUpdateMdseInfo(MdseInfo mdseInfo) {
        mdseSearchFeignClient.updateMdse(mdseInfo);
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticDeleteMdseInfo(String ids) {
        mdseSearchFeignClient.deleteMdse(ids);
    }

    @Async
    @Override
    @SneakyThrows
    public void syncToElasticUpdateMdseInfoList(List<Long> mdseIds) {
        List<MallMdse> mallMdseList = findAllById(mdseIds);
        List<MdseInfo> mdseInfoList = list2vo(mallMdseList);
        mdseSearchFeignClient.updateMdseList(mdseInfoList);
    }

    @Override
    public List<MallMdse> findAll() {
        return mdseRepository.findAll();
    }

    @Override
    public List<MallMdse> findAllByShopIds(List<Long> lids) {
        return mdseRepository.findAllByShopIdIn(lids);
    }

    @Override
    public Page<MdsePurchaseRecord> findBuyerPage(BuyerPageQuery buyerPageQuery) {
        Pageable pageable = PageUtils.getPageable(buyerPageQuery);

        Specification<MdsePurchaseRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (!ObjectUtils.isEmpty(buyerPageQuery.getUserId())) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), buyerPageQuery.getUserId()));

            }
            if (!ObjectUtils.isEmpty(buyerPageQuery.getMdseId())) {
                list.add(criteriaBuilder.equal(root.get("mdseId").as(Long.class), buyerPageQuery.getUserId()));
            }

            if (!ObjectUtils.isEmpty(buyerPageQuery.getType())) {
                list.add(criteriaBuilder.equal(root.get("type").as(Long.class), buyerPageQuery.getType()));
            }


            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };
        return purchaseRecordRepository.findAll(spec, pageable);
    }

    @Override
    public List<MdsePurchaseRecordInfo> purchaseRecordList2vo(List<MdsePurchaseRecord> entityList) {
        List<CompletableFuture<MdsePurchaseRecordInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> purchaseRecordEntity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private MdsePurchaseRecordInfo purchaseRecordEntity2vo(MdsePurchaseRecord entity) {
        if (Objects.nonNull(entity)) {
            MdsePurchaseRecordInfo info = new MdsePurchaseRecordInfo();
            Optional<MallMdse> mdseOptional = findById(entity.getMdseId());
            mdseOptional.ifPresent(mdseEntity -> info.setMdseInfo(entity2vo(mdseEntity)));
            Result<UserInfo> userInfoResult = userFeignClient.getUserById(entity.getUserId());
            if (Result.isSuccess(userInfoResult)) {
                info.setUserInfo(userInfoResult.getData());
            }
            return info;
        }
        return null;
    }

    @Override
    public void addMdsePurchaseRecord(MdsePurchaseRecordDTO recordDTO) {
        MdsePurchaseRecord purchaseRecord = new MdsePurchaseRecord();
        purchaseRecord.setOrderId(recordDTO.getOrderId());
        purchaseRecord.setMdseId(recordDTO.getMdseId());
        purchaseRecord.setUserId(recordDTO.getUserId());
        purchaseRecord.setType(recordDTO.getType());
        purchaseRecord.setDeleted(SystemConstants.DELETED_NO);
        purchaseRecordRepository.save(purchaseRecord);
    }

    @Override
    public List<MdsePurchaseRecord> getAllPurchaseRecordByMdseId(Long mdseId) {
        return purchaseRecordRepository.findAllByMdseId(mdseId);
    }

    @Override
    public List<MdsePurchaseRecord> getAllPurchaseRecordByType(Integer type) {
        return purchaseRecordRepository.findAllByType(type);
    }

    @Override
    public Optional<MdseCardRules> findCardRulesByMdseId(Long mdseId) {
        return cardRulesRepository.findByCardId(mdseId);
    }

}
