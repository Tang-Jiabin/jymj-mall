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
import com.jymj.mall.mdse.repository.MdseRepository;
import com.jymj.mall.mdse.repository.ShopMdseMapRepository;
import com.jymj.mall.mdse.service.*;
import com.jymj.mall.mdse.vo.*;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.ShopInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.stream.Collectors;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallMdse add(MdseDTO dto) {

        MallMdse mdse = new MallMdse();

        mdse.setName(dto.getName());
        mdse.setNumber(dto.getNumber());
        mdse.setPrice(dto.getPrice());
        mdse.setInventoryQuantity(dto.getInventoryQuantity());
        mdse.setStartingQuantity(dto.getStartingQuantity());
        mdse.setRemainingQuantity(dto.getInventoryQuantity());
        mdse.setShowRemainingQuantity(dto.getShowRemainingQuantity());
        mdse.setRefund(dto.getRefund());
        mdse.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        mdse.setButtonName(dto.getButtonName());
        mdse.setDetails(dto.getDetails());
        mdse.setSalesVolume(0);
        mdse.setMallId(dto.getMallId());
        mdse.setDeleted(SystemConstants.DELETED_NO);
        mdse.setStatus(dto.getStatus() == null ? 2 : dto.getStatus());

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

        //商品图片
        List<PictureDTO> pictureList = dto.getPictureList();
        for (PictureDTO pictureDTO : pictureList) {
            PictureDTO picture = new PictureDTO();
            picture.setUrl(pictureDTO.getUrl());
            picture.setType(PictureType.MDSE_PIC);
            picture.setMdseId(mdse.getMdseId());
            pictureService.add(picture);
        }

        //商品视频
        List<PictureDTO> videoList = dto.getVideoList();
        if (!CollectionUtils.isEmpty(videoList)) {
            for (PictureDTO pictureDTO : videoList) {
                PictureDTO picture = new PictureDTO();
                picture.setUrl(pictureDTO.getUrl());
                picture.setType(PictureType.MDSE_VIDEO);
                picture.setMdseId(mdse.getMdseId());
                pictureService.add(picture);
            }
        }


        //商品标签
        Set<Long> labelIdList = dto.getLabelIdList();
        if (!CollectionUtils.isEmpty(labelIdList)) {
            List<MdseLabel> labelList = labelService.findAllById(Lists.newArrayList(labelIdList));
            labelService.addMdseLabelMap(mdse.getMdseId(), labelList.stream().map(MdseLabel::getLabelId).collect(Collectors.toList()));
        }

        //商品分组
        Set<Long> groupIdList = dto.getGroupIdList();
        if (!CollectionUtils.isEmpty(groupIdList)) {
            List<MdseGroup> groupList = groupService.findAllById(Lists.newArrayList(groupIdList));
            groupService.addMdseGroupMap(mdse.getMdseId(), groupList.stream().map(MdseGroup::getGroupId).collect(Collectors.toList()));
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
                    shopMdseMap.setMdseId(dto.getMdseId());
                    shopMdseMap.setShopId(shopInfo.getShopId());
                    shopMdseMap.setDeleted(SystemConstants.DELETED_NO);
                    shopMdseMapList.add(shopMdseMap);
                }
                shopMdseMapRepository.saveAll(shopMdseMapList);
            }
        }

        //商品库存
        Long mdseId = mdse.getMdseId();
        Set<StockDTO> stockList = dto.getStockList();
        Optional.of(stockList)
                .orElse(Sets.newHashSet())
                .stream()
                .filter(stock -> !ObjectUtils.isEmpty(stock))
                .forEach(stockDTO -> {
                    stockDTO.setMdseId(mdseId);
                    stockService.add(stockDTO);
                });


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

        pictureService.updateMdsePicture(dto.getPictureList(), dto.getMdseId(), PictureType.MDSE_PIC);

        pictureService.updateMdsePicture(dto.getVideoList(), dto.getMdseId(), PictureType.MDSE_VIDEO);

        updateMdseGroup(dto);

        updateMdseShop(dto);

        updateMdseLabel(dto);

        updateMdseStock(dto);

        return Optional.of(mdseRepository.save(mallMdse));
    }

    private void updateMdseStock(MdseDTO dto) {
        Set<StockDTO> stockList = dto.getStockList();
        List<MdseStock> mdseStockList = stockService.findAllByMdseId(dto.getMdseId());
        //需要删除的库存
        List<MdseStock> deleteMdseStockList = mdseStockList.stream().filter(mdseStock -> !stockList.stream().map(StockDTO::getStockId).filter(Objects::nonNull).collect(Collectors.toList()).contains(mdseStock.getStockId())).collect(Collectors.toList());
        stockService.deleteMdseStock(deleteMdseStockList);

        //需要添加的库存
        List<StockDTO> addMdseStockList = stockList.stream().filter(stockDTO -> {
            if (stockDTO.getStockId() == null || stockDTO.getStockId() == 0L) {
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


    private void updateMdseLabel(MdseDTO dto) {
        Set<Long> labelIdList = dto.getLabelIdList();
        List<MallMdseLabelMap> mdseLabelMapList = labelService.findMdseLabelAllByMdseId(dto.getMdseId());
        //需要删除的标签
        List<MallMdseLabelMap> deleteMdseLabelMapList = mdseLabelMapList.stream().filter(mdseLabel -> !labelIdList.contains(mdseLabel.getLabelId())).collect(Collectors.toList());
        labelService.deleteMdseLabel(deleteMdseLabelMapList);
        //需要添加的标签
        List<Long> addMdseLabelIdList = labelIdList.stream().filter(labelId -> !mdseLabelMapList.stream().map(MallMdseLabelMap::getLabelId).collect(Collectors.toList()).contains(labelId)).collect(Collectors.toList());
        labelService.addMdseLabelMap(dto.getMdseId(), addMdseLabelIdList);
    }

    private void updateMdseShop(MdseDTO dto) {
        Set<Long> shopIdList = dto.getShopIdList();
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

    private void updateMdseGroup(MdseDTO dto) {
        Set<Long> groupIdList = dto.getGroupIdList();
        List<MallMdseGroupMap> mdseGroupList = groupService.findMdseGroupAllByMdseId(dto.getMdseId());
        //需要删除的分组
        List<MallMdseGroupMap> deleteMdseGroupList = mdseGroupList.stream().filter(mdseGroup -> !groupIdList.contains(mdseGroup.getGroupId())).collect(Collectors.toList());
        groupService.deleteMdseGroupAll(deleteMdseGroupList);
        //需要添加的分组
        List<Long> addMdseGroupIdList = groupIdList.stream().filter(id -> !mdseGroupList.stream().map(MallMdseGroupMap::getGroupId).collect(Collectors.toList()).contains(id)).collect(Collectors.toList());
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
        return entity2vo(entity, true, true, true, true, true, true, true);
    }

    @Override
    public List<MdseInfo> list2vo(List<MallMdse> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public MdseInfo entity2vo(MallMdse entity, boolean group, boolean stock, boolean label, boolean picture, boolean mfg, boolean type, boolean brand) {
        MdseInfo mdseInfo = new MdseInfo();
        mdseInfo.setMdseId(entity.getMdseId());
        mdseInfo.setName(entity.getName());
        mdseInfo.setNumber(entity.getNumber());
        mdseInfo.setPrice(entity.getPrice());
        mdseInfo.setInventoryQuantity(entity.getInventoryQuantity());
        mdseInfo.setRemainingQuantity(entity.getRemainingQuantity());
        mdseInfo.setStartingQuantity(entity.getStartingQuantity());
        mdseInfo.setShowRemainingQuantity(entity.isShowRemainingQuantity());
        mdseInfo.setRefund(entity.isRefund());
        mdseInfo.setInventoryReductionMethod(entity.getInventoryReductionMethod());
        mdseInfo.setButtonName(entity.getButtonName());
        mdseInfo.setDetails(entity.getDetails());
        mdseInfo.setSalesVolume(entity.getSalesVolume());
        mdseInfo.setCreateTime(entity.getCreateTime());
        mdseInfo.setStatus(entity.getStatus());


        if (group) {
            mdseInfo = voAddGroupList(mdseInfo);
        }
        if (stock) {
            mdseInfo = voAddStockList(mdseInfo);
        }
        if (label) {
            mdseInfo = voAddLabelList(mdseInfo);
        }
        if (picture) {
            mdseInfo = voAddPictureList(mdseInfo);
        }
        if (mfg) {
            mdseInfo = voAddMfg(mdseInfo, entity.getMfgId());
        }
        if (type) {
            mdseInfo = voAddType(mdseInfo, entity.getTypeId());
        }
        if (brand) {
            mdseInfo = voAddBrand(mdseInfo, entity.getBrandId());
        }

        return mdseInfo;
    }

    @Override
    public List<MdseInfo> list2vo(List<MallMdse> entityList, boolean group, boolean stock, boolean label, boolean picture, boolean mfg, boolean type, boolean brand) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> entity2vo(entity, group, stock, label, picture, mfg, type, brand))
                .collect(Collectors.toList());
    }

    @Override
    public List<MallMdse> findAllById(List<Long> idList) {

        return mdseRepository.findAllById(idList);
    }

    @Override
    public Page<MallMdse> findPage(MdsePageQuery mdsePageQuery) {

        Pageable pageable = PageUtils.getPageable(mdsePageQuery);

        Specification<MallMdse> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

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

            //商场id
            if (mdsePageQuery.getMallId() != null) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Integer.class), mdsePageQuery.getMallId()));
            }

            //店铺id
            if (mdsePageQuery.getShopId() != null) {
                List<ShopMdseMap> shopMdseMapList = shopMdseMapRepository.findAllByShopId(mdsePageQuery.getShopId());
                CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
                shopMdseMapList.forEach(mdseLabel -> mdseIdIn.value(mdseLabel.getMdseId()));
                mdseIdIn.value(0L);
                list.add(mdseIdIn);

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
                CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
                mdseGroupMaps.forEach(mdseGroup -> mdseIdIn.value(mdseGroup.getMdseId()));
                mdseIdIn.value(0L);
                list.add(mdseIdIn);
            }

            //标签
            if (!ObjectUtils.isEmpty(mdsePageQuery.getLabelId())) {
                List<MallMdseLabelMap> mdseLabelMaps = labelService.findMdseLabelAllByLabelId(mdsePageQuery.getLabelId());
                CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
                mdseLabelMaps.forEach(mdseLabel -> mdseIdIn.value(mdseLabel.getMdseId()));
                mdseIdIn.value(0L);
                list.add(mdseIdIn);
            }

            //类型
            if (!ObjectUtils.isEmpty(mdsePageQuery.getTypeId())) {
                list.add(criteriaBuilder.equal(root.get("typeId").as(Integer.class), mdsePageQuery.getTypeId()));
            }

            //库存减少方式
            if (!ObjectUtils.isEmpty(mdsePageQuery.getInventoryReductionMethod())) {
                list.add(criteriaBuilder.equal(root.get("inventoryReductionMethod").as(InventoryReductionMethod.class), mdsePageQuery.getInventoryReductionMethod()));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and((Predicate[]) list.toArray(p));
        };
        return mdseRepository.findAll(spec, pageable);
    }



    @Override
    public MdseInfo voAddPictureList(MdseInfo mdseInfo) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId())) {
            List<MallPicture> pictureList = pictureService.findAllByMdseId(mdseInfo.getMdseId());
            List<PictureInfo> pictureInfoList = pictureService.list2vo(pictureList);
            mdseInfo.setPictureList(pictureInfoList);
        }
        return mdseInfo;
    }

    @Override
    public MdseInfo voAddGroupList(MdseInfo mdseInfo) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId())) {
            List<MdseGroup> groupList = groupService.findAllByMdseId(mdseInfo.getMdseId());
            List<GroupInfo> groupInfoList = groupService.list2vo(groupList);
            mdseInfo.setGroupList(groupInfoList);
        }
        return mdseInfo;
    }

    @Override
    public MdseInfo voAddBrand(MdseInfo mdseInfo, Long brandId) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId()) && !ObjectUtils.isEmpty(brandId)) {
            Optional<MdseBrand> brandOptional = brandService.findById(brandId);
            brandOptional.ifPresent(brand -> {
                mdseInfo.setBrand(brandService.entity2vo(brand));
            });
        }
        return mdseInfo;
    }

    @Override
    public MdseInfo voAddMfg(MdseInfo mdseInfo, Long mfgId) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId()) && !ObjectUtils.isEmpty(mfgId)) {
            Optional<MdseMfg> mfgOptional = mfgService.findById(mfgId);
            mfgOptional.ifPresent(mfg -> {
                mdseInfo.setMfg(mfgService.entity2vo(mfg));
            });
        }
        return mdseInfo;
    }

    @Override
    public MdseInfo voAddStockList(MdseInfo mdseInfo) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId())) {
            List<MdseStock> stockList = stockService.findAllByMdseId(mdseInfo.getMdseId());
            List<StockInfo> stockInfos = stockService.list2vo(stockList);
            mdseInfo.setStockList(stockInfos);
        }
        return mdseInfo;
    }

    @Override
    public MdseInfo voAddType(MdseInfo mdseInfo, Long typeId) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId()) && !ObjectUtils.isEmpty(typeId)) {
            Optional<MdseType> typeOptional = typeService.findById(typeId);
            typeOptional.ifPresent(type -> {
                mdseInfo.setTypeInfo(typeService.entity2vo(type));
            });
        }
        return mdseInfo;
    }

    @Override
    public MdseInfo voAddLabelList(MdseInfo mdseInfo) {
        if (!ObjectUtils.isEmpty(mdseInfo) && !ObjectUtils.isEmpty(mdseInfo.getMdseId())) {
            List<MdseLabel> labelList = labelService.findAllByMdseId(mdseInfo.getMdseId());
            List<LabelInfo> labelInfoList = labelService.list2vo(labelList);
            mdseInfo.setLabelInfoList(labelInfoList);
        }

        return mdseInfo;
    }

}
