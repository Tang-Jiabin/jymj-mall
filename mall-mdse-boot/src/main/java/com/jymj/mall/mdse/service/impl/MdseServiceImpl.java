package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.dto.*;
import com.jymj.mall.mdse.entity.*;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.*;
import com.jymj.mall.mdse.service.*;
import com.jymj.mall.mdse.vo.*;
import com.jymj.mall.search.api.MdseSearchFeignClient;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.ShopInfo;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.vo.UserInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
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
    private final ShopMdseMapRepository shopMdseMapRepository;
    private final MdseSearchFeignClient mdseSearchFeignClient;
    private final ThreadPoolTaskExecutor executor;
    private final MdseCardRulesRepository cardRulesRepository;
    private final CardMdseRepository cardMdseRepository;
    private final UserFeignClient userFeignClient;
    private final MdsePurchaseRecordRepository purchaseRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallMdse add(MdseDTO dto) {

        MallMdse mdse = new MallMdse();

        mdse.setName(dto.getName());
        mdse.setNumber(dto.getNumber());
        mdse.setPrice(dto.getPrice());
        mdse.setPostage(dto.getPostage());
        mdse.setInventoryQuantity(dto.getInventoryQuantity());
        mdse.setStartingQuantity(dto.getStartingQuantity());
        mdse.setRemainingQuantity(dto.getInventoryQuantity());
        mdse.setShowRemainingQuantity(Objects.nonNull(dto.getShowRemainingQuantity()) ? dto.getShowRemainingQuantity() : false);
        mdse.setRefund(dto.getRefund());
        mdse.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        mdse.setButtonName(dto.getButtonName());
        mdse.setDetails(dto.getDetails());
        mdse.setSalesVolume(0);
        mdse.setMallId(dto.getMallId());
        mdse.setDeleted(SystemConstants.DELETED_NO);
        mdse.setStatus(dto.getStatus() == null ? 2 : dto.getStatus());
        mdse.setClassify(1);
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

        //商品所属店铺
        Set<Long> shopIdList = dto.getShopIdList();
        if (!CollectionUtils.isEmpty(shopIdList)) {
            Result<List<ShopInfo>> shopInfoListResult = shopFeignClient.getAllById(StringUtils.collectionToCommaDelimitedString(shopIdList));
            if (Result.isSuccess(shopInfoListResult)) {
                List<ShopInfo> shopInfoList = shopInfoListResult.getData();
                List<ShopMdseMap> shopMdseMapList = Lists.newArrayList();
                for (ShopInfo shopInfo : shopInfoList) {
                    ShopMdseMap shopMdseMap = new ShopMdseMap();
                    shopMdseMap.setMdseId(mdseId);
                    shopMdseMap.setShopId(shopInfo.getShopId());
                    shopMdseMap.setDeleted(SystemConstants.DELETED_NO);
                    shopMdseMapList.add(shopMdseMap);
                }
                shopMdseMapRepository.saveAll(shopMdseMapList);
            }
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

        if (!CollectionUtils.isEmpty(dto.getCardMdseList())) {
            Set<CardMdseDTO> cardMdseDTOList = dto.getCardMdseList();
            List<CardMdse> cardMdseList = Lists.newArrayList();
            for (CardMdseDTO cardMdseDTO : cardMdseDTOList) {
                CardMdse cardMdse = new CardMdse();
                cardMdse.setCardId(mdseId);
                cardMdse.setMdseId(cardMdseDTO.getMdseId());
                cardMdse.setStockId(cardMdseDTO.getStockId());
                cardMdse.setDeleted(SystemConstants.DELETED_NO);
                cardMdseList.add(cardMdse);
            }
            cardMdseRepository.saveAll(cardMdseList);
        }
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
        //剩余库存数量
        if (!ObjectUtils.isEmpty(dto.getInventoryQuantity())) {
            mallMdse.setRemainingQuantity(dto.getInventoryQuantity());
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

        //卡商品信息
        if (!CollectionUtils.isEmpty(dto.getCardMdseList())) {
            Set<CardMdseDTO> cardMdseDTOList = dto.getCardMdseList();
            List<CardMdse> allByCardId = cardMdseRepository.findAllByCardId(mallMdse.getMdseId());
            cardMdseRepository.deleteAll(allByCardId);
            List<CardMdse> cardMdseList = Lists.newArrayList();
            for (CardMdseDTO cardMdseDTO : cardMdseDTOList) {
                CardMdse cardMdse = new CardMdse();
                cardMdse.setCardId(mallMdse.getMdseId());
                cardMdse.setMdseId(cardMdseDTO.getMdseId());
                cardMdse.setStockId(cardMdseDTO.getStockId());
                cardMdse.setDeleted(SystemConstants.DELETED_NO);
                cardMdseList.add(cardMdse);
            }
            cardMdseRepository.saveAll(cardMdseList);
        }

        if (!CollectionUtils.isEmpty(dto.getPictureList())) {
            pictureService.updateMdsePicture(dto.getPictureList(), dto.getMdseId(), PictureType.MDSE_PIC);
        }
        if (!CollectionUtils.isEmpty(dto.getVideoList())) {
            pictureService.updateMdsePicture(dto.getVideoList(), dto.getMdseId(), PictureType.MDSE_VIDEO);
        }
        updateMdseGroup(dto);

        updateMdseShop(dto);

        updateMdseLabel(dto);

        updateMdseStock(dto);

        return Optional.of(mdseRepository.save(mallMdse));
    }

    private void updateMdseStock(MdseDTO dto) {

        Set<StockDTO> stockList = dto.getStockList();
        if (!ObjectUtils.isEmpty(stockList)) {
            List<MdseStock> mdseStockList = stockService.findAllByMdseId(dto.getMdseId());
            //需要删除的库存
            List<MdseStock> deleteMdseStockList = mdseStockList.stream().filter(mdseStock -> !stockList.stream().map(StockDTO::getStockId).filter(Objects::nonNull).collect(Collectors.toList()).contains(mdseStock.getStockId())).collect(Collectors.toList());
            stockService.deleteMdseStock(deleteMdseStockList);

            //需要添加的库存
            List<StockDTO> addMdseStockList = stockList.stream().filter(stockDTO -> {
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
            List<StockDTO> updateMdseStockList = stockList.stream().filter(stockDTO -> mdseStockList.stream().map(MdseStock::getStockId).collect(Collectors.toList()).contains(stockDTO.getStockId())).collect(Collectors.toList());
            updateMdseStockList.forEach(stockService::update);
        }

    }


    private void updateMdseLabel(MdseDTO dto) {
        Set<Long> labelIdList = dto.getLabelIdList();
        if (!ObjectUtils.isEmpty(labelIdList)) {
            List<MallMdseLabelMap> mdseLabelMapList = labelService.findMdseLabelAllByMdseId(dto.getMdseId());
            //需要删除的标签
            List<MallMdseLabelMap> deleteMdseLabelMapList = mdseLabelMapList.stream().filter(mdseLabel -> !labelIdList.contains(mdseLabel.getLabelId())).collect(Collectors.toList());
            labelService.deleteMdseLabel(deleteMdseLabelMapList);
            //需要添加的标签
            List<Long> addMdseLabelIdList = labelIdList.stream().filter(labelId -> !mdseLabelMapList.stream().map(MallMdseLabelMap::getLabelId).collect(Collectors.toList()).contains(labelId)).collect(Collectors.toList());
            labelService.addMdseLabelMap(dto.getMdseId(), addMdseLabelIdList);
        }

    }

    private void updateMdseShop(MdseDTO dto) {
        Set<Long> shopIdList = dto.getShopIdList();
        if (!ObjectUtils.isEmpty(shopIdList)) {
            List<ShopMdseMap> shopMdseMapList = shopMdseMapRepository.findAllByMdseId(dto.getMdseId());
            //需要删除的店铺
            List<ShopMdseMap> deleteShopMdseList = shopMdseMapList.stream().filter(shopMdse -> !shopIdList.contains(shopMdse.getShopId())).collect(Collectors.toList());
            shopMdseMapRepository.deleteAll(deleteShopMdseList);
            //需要添加的店铺
            List<Long> addShopIdList = shopIdList.stream().filter(shopId -> !shopMdseMapList.stream().map(ShopMdseMap::getShopId).collect(Collectors.toList()).contains(shopId)).collect(Collectors.toList());
            List<ShopMdseMap> shopMdseMaps = Lists.newArrayList();
            for (Long shopId : addShopIdList) {
                ShopMdseMap shopMdseMap = new ShopMdseMap();
                shopMdseMap.setMdseId(dto.getMdseId());
                shopMdseMap.setShopId(shopId);
                shopMdseMap.setDeleted(SystemConstants.DELETED_NO);
                shopMdseMaps.add(shopMdseMap);
            }
            shopMdseMapRepository.saveAll(shopMdseMaps);
        }

    }

    private void updateMdseGroup(MdseDTO dto) {
        Set<Long> groupIdList = dto.getGroupIdList();
        if (!ObjectUtils.isEmpty(groupIdList)) {
            List<MallMdseGroupMap> mdseGroupList = groupService.findMdseGroupAllByMdseId(dto.getMdseId());
            //需要删除的分组
            List<MallMdseGroupMap> deleteMdseGroupList = mdseGroupList.stream().filter(mdseGroup -> !groupIdList.contains(mdseGroup.getGroupId())).collect(Collectors.toList());
            groupService.deleteMdseGroupAll(deleteMdseGroupList);
            //需要添加的分组
            List<Long> addMdseGroupIdList = groupIdList.stream().filter(id -> !mdseGroupList.stream().map(MallMdseGroupMap::getGroupId).collect(Collectors.toList()).contains(id)).collect(Collectors.toList());
            groupService.addMdseGroupMap(dto.getMdseId(), addMdseGroupIdList);
        }

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
        MdseInfoShow show = new MdseInfoShow();
        show.setGroup(1);
        show.setStock(1);
        show.setLabel(1);
        show.setMfg(1);
        show.setType(1);
        show.setBrand(1);
        show.setShop(1);
        show.setPicture(1);

        return entity2vo(entity, show);
    }

    @Override
    public List<MdseInfo> list2vo(List<MallMdse> entityList) {
        List<CompletableFuture<MdseInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
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
        if (entity.getClassify().equals(2)) {
            Optional<MdseCardRules> rulesOptional = cardRulesRepository.findByCardId(entity.getMdseId());
            rulesOptional.ifPresent(rule -> mdseInfo.setEffectiveRules(rule2vo(rule)));

            List<CardMdse> cardMdseList = cardMdseRepository.findAllByCardId(entity.getMdseId());
            List<MallMdse> mdseList = findAllById(cardMdseList.stream().map(CardMdse::getMdseId).collect(Collectors.toList()));
            List<MdseInfo> mdseInfoList = list2vo(mdseList, MdseInfoShow.builder().stock(1).shop(1).picture(1).build());
            mdseInfoList = mdseInfoList.stream().map(mdseInfo2 -> {
                List<StockInfo> stockList = mdseInfo2.getStockList();
                List<StockInfo> stockInfoList = stockList.stream().filter(stockInfo -> cardMdseList.stream().map(CardMdse::getStockId).collect(Collectors.toList()).contains(stockInfo.getStockId())).collect(Collectors.toList());
                mdseInfo2.setStockList(stockInfoList);
                return mdseInfo2;
            }).collect(Collectors.toList());
            mdseInfo.setMdseInfoList(mdseInfoList);
        }
        return mdseInfo;
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
        if (SystemConstants.STATUS_OPEN.equals(show.getBrand()) && !ObjectUtils.isEmpty(entity.getTypeId())) {
            Optional<MdseBrand> brandOptional = brandService.findById(entity.getTypeId());
            if (brandOptional.isPresent()) {
                MdseBrand mdseBrand = brandOptional.get();
                mdseInfo.setBrand(brandService.entity2vo(mdseBrand));
            }
        }
        if (SystemConstants.STATUS_OPEN.equals(show.getShop()) && !ObjectUtils.isEmpty(entity.getMdseId())) {
            List<Long> shopIdList = shopMdseMapRepository.findAllByMdseId(entity.getMdseId()).stream().map(ShopMdseMap::getShopId).collect(Collectors.toList());
            mdseInfo.setShopInfoList(getShopInfoList(shopIdList));
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
        mdseInfo.setInventoryQuantity(entity.getInventoryQuantity());
        mdseInfo.setRemainingQuantity(entity.getRemainingQuantity());
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
        return mdseInfo;
    }

    private List<ShopInfo> getShopInfoList(List<Long> shopIdList) {
        if (!CollectionUtils.isEmpty(shopIdList)) {
            List<CompletableFuture<Result<ShopInfo>>> futureList = shopIdList.stream()
                    .map(shopId -> CompletableFuture.supplyAsync(() -> shopFeignClient.getShopById(shopId), executor))
                    .collect(Collectors.toList());
            return futureList.stream()
                    .map(CompletableFuture::join)
                    .filter(Result::isSuccess)
                    .map(Result::getData)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
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

            CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
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
                list.add(criteriaBuilder.equal(root.get("brandId").as(Integer.class), mdsePageQuery.getBrandId()));
            }

            //厂家id
            if (mdsePageQuery.getMfgId() != null) {
                list.add(criteriaBuilder.equal(root.get("mfgId").as(Integer.class), mdsePageQuery.getMfgId()));
            }

            //商场id
            if (mdsePageQuery.getMallId() != null) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Integer.class), mdsePageQuery.getMallId()));
            }

            //店铺id
            if (mdsePageQuery.getShopId() != null) {
                List<ShopMdseMap> shopMdseMapList = shopMdseMapRepository.findAllByShopId(mdsePageQuery.getShopId());
                shopMdseMapList.forEach(mdseLabel -> mdseIdSet.add(mdseLabel.getMdseId()));
                mdseIdSet.add(0L);
            }

            if (!ObjectUtils.isEmpty(mdsePageQuery.getUserId())) {
                List<MdsePurchaseRecord> purchaseRecordList = Lists.newArrayList();
                if (!ObjectUtils.isEmpty(mdsePageQuery.getTypeId())) {
                    purchaseRecordList = purchaseRecordRepository.findAllByUserIdAndType(mdsePageQuery.getUserId(), mdsePageQuery.getTypeId());
                } else {
                    purchaseRecordList = purchaseRecordRepository.findAllByUserId(mdsePageQuery.getUserId());
                }
                purchaseRecordList.forEach(purchaseRecord -> mdseIdSet.add(purchaseRecord.getMdseId()));
                mdseIdSet.add(0L);
            }


            //数量大于等于
            if (mdsePageQuery.getQuantityGreaterThanOrEqual() != null) {
                list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("remainingQuantity").as(Integer.class), mdsePageQuery.getQuantityGreaterThanOrEqual()));
            }

            //数量小于等于
            if (mdsePageQuery.getQuantityLessThanOrEqual() != null) {
                list.add(criteriaBuilder.lessThanOrEqualTo(root.get("remainingQuantity").as(Integer.class), mdsePageQuery.getQuantityLessThanOrEqual()));
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
                mdseIdSet.forEach(mdseIdIn::value);
                list.add(mdseIdIn);
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
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
        List<ShopMdseMap> shopMdseMapList = shopMdseMapRepository.findAllByShopIdIn(lids);
        List<Long> mdseIdList = shopMdseMapList.stream().map(ShopMdseMap::getMdseId).filter(mdseId -> !ObjectUtils.isEmpty(mdseId)).collect(Collectors.toList());
        return mdseRepository.findAllById(mdseIdList);
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

}
