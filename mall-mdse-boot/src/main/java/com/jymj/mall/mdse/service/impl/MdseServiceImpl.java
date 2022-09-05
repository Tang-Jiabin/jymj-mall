package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.entity.*;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.MdseRepository;
import com.jymj.mall.mdse.service.*;
import com.jymj.mall.mdse.vo.*;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallMdse add(MdseDTO dto) {

        verifyShopId(dto.getShopId());

        MallMdse mdse = new MallMdse();

        mdse.setName(dto.getName());
        mdse.setNumber(dto.getNumber());
        mdse.setPrice(dto.getPrice());
        mdse.setInventoryQuantity(dto.getInventoryQuantity());
        mdse.setStartingQuantity(dto.getStartingQuantity());
        mdse.setRemainingQuantity(dto.getInventoryQuantity());
        mdse.setShowRemainingQuantity(dto.isShowRemainingQuantity());
        mdse.setRefund(dto.isRefund());
        mdse.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        mdse.setButtonName(dto.getButtonName());
        mdse.setDetails(dto.getDetails());
        mdse.setSalesVolume(0);
        mdse.setShopId(dto.getShopId());
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
        Set<String> pictureList = dto.getPictureList();
        for (String url : pictureList) {
            PictureDTO picture = new PictureDTO();
            picture.setUrl(url);
            picture.setType(PictureType.MDSE_PIC);
            picture.setMdseId(mdse.getMdseId());
            pictureService.add(picture);
        }

        //商品标签
        Set<Long> labelIdList = dto.getLabelIdList();
        if (!CollectionUtils.isEmpty(labelIdList)) {
            List<MdseLabel> labelList = labelService.findAllById(Lists.newArrayList(labelIdList));
            labelService.addMdseLabelMap(mdse.getMdseId(), labelList);
        }
        //商品分组
        Set<Long> groupIdList = dto.getGroupIdList();
        if (!CollectionUtils.isEmpty(groupIdList)) {
            List<MdseGroup> groupList = groupService.findAllById(Lists.newArrayList(groupIdList));
            groupService.addMdseGroupMap(mdse.getMdseId(), groupList);
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
    public Optional<MallMdse> update(MdseDTO dto) {
        return Optional.empty();
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
    public Page<MallMdse> findPage(MdsePageQuery mdsePageQuery) {

        Pageable pageable = PageUtils.getPageable(mdsePageQuery);

        List<Long> shopIdList = findAllShopIdByAuth();


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

            //分组
            if (!ObjectUtils.isEmpty(mdsePageQuery.getGroupId())) {
                List<MallMdseGroupMap> mdseGroupMaps = groupService.findAllMdseGroupById(mdsePageQuery.getGroupId());
                CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
                mdseGroupMaps.forEach(mdseGroup-> mdseIdIn.value(mdseGroup.getMdseId()));
                mdseIdIn.value(0L);
                list.add(mdseIdIn);
            }

            //标签
            if (!ObjectUtils.isEmpty(mdsePageQuery.getLabelId())) {
                List<MallMdseLabelMap> mdseLabelMaps = labelService.findAllMdseLabelByLabelId(mdsePageQuery.getLabelId());
                CriteriaBuilder.In<Long> mdseIdIn = criteriaBuilder.in(root.get("mdseId").as(Long.class));
                mdseLabelMaps.forEach(mdseLabel-> mdseIdIn.value(mdseLabel.getMdseId()));
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


            CriteriaBuilder.In<Long> shopIdIn = criteriaBuilder.in(root.get("shopId").as(Long.class));
            shopIdList.forEach(shopIdIn::value);
            shopIdIn.value(0L);
            list.add(shopIdIn);

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and((Predicate[]) list.toArray(p));
        };
        return mdseRepository.findAll(spec, pageable);
    }

    @NotNull
    private List<Long> findAllShopIdByAuth() {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (!Result.isSuccess(shopListResult)) {
            throw new BusinessException("店铺权限信息获取失败");
        }
        List<Long> shopIdList = shopListResult.getData().stream().map(ShopInfo::getShopId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(shopIdList)) {
            throw new BusinessException("权限不足");
        }
        return shopIdList;
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

    private void verifyShopId(Long shopId) {
        if (ObjectUtils.isEmpty(shopId)) {
            throw new BusinessException("店铺信息获取失败");
        }
        List<Long> shopIdList = findAllShopIdByAuth();
        if (!shopIdList.contains(shopId)) {
            throw new BusinessException("没有店铺【 " + shopId + " 】的操作权限");
        }
    }
}
