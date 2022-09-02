package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.dto.SpecDTO;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.entity.*;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.MdseRepository;
import com.jymj.mall.mdse.service.*;
import com.jymj.mall.mdse.vo.MdseInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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


    @Override
    @Transactional(rollbackFor = Exception.class)
//    @GlobalTransactional(name = "mall_mdse_mdse_add", rollbackFor = Exception.class)
    public MallMdse add(MdseDTO dto) {
        MallMdse mdse = new MallMdse();

        mdse.setName(dto.getName());
        mdse.setNumber(dto.getNumber());
        mdse.setPrice(dto.getPrice());
        mdse.setInventoryQuantity(dto.getInventoryQuantity());
        mdse.setStartingQuantity(dto.getStartingQuantity());
        mdse.setShowRemainingQuantity(dto.isShowRemainingQuantity());
        mdse.setRefund(dto.isRefund());
        mdse.setInventoryReductionMethod(dto.getInventoryReductionMethod());
        mdse.setButtonName(dto.getButtonName());
        mdse.setDetails(dto.getDetails());
        mdse.setDeleted(SystemConstants.DELETED_NO);

        //商品图片
        List<String> pictureList = dto.getPictureList();
        List<MallPicture> mallPictures = Lists.newArrayList();
        for (String link : pictureList) {
            MallPicture picture = new MallPicture();
            picture.setLink(link);
            picture.setType(PictureType.MDSE_PIC);
            picture.setDeleted(SystemConstants.DELETED_NO);
            mallPictures.add(picture);
        }
        mdse.setPictureList(mallPictures);

        //商品分组
        List<Long> groupIdList = dto.getGroupIdList();
        List<MdseGroup> groupList = groupService.findAllById(groupIdList);
        mdse.setGroupList(groupList);

        //商品品牌
        Long brandId = dto.getBrandId();
        Optional<MdseBrand> brandOptional = brandService.findById(brandId);
        brandOptional.ifPresent(mdse::setBrand);
        System.out.println(brandOptional.get());

        //商品厂家
        Long mfgId = dto.getMfgId();
        Optional<MdseMfg> mfgOptional = mfgService.findById(mfgId);
        mfgOptional.ifPresent(mdse::setMfg);

        //商品库存
        List<StockDTO> stockList = dto.getStockList();
        List<MdseStock> mdseStockList = Lists.newArrayList();
        for (StockDTO stockDTO : stockList) {
            MdseStock stock = new MdseStock();

            SpecDTO specA = stockDTO.getSpecA();
            if (!ObjectUtils.isEmpty(specA)) {
                MdseSpec mdseSpecA = new MdseSpec();
                mdseSpecA.setKey(specA.getKey());
                mdseSpecA.setValue(specA.getValue());
                mdseSpecA.setDeleted(SystemConstants.DELETED_NO);
            }


            SpecDTO specB = stockDTO.getSpecB();
            if (!ObjectUtils.isEmpty(specB)) {
                MdseSpec mdseSpecB = new MdseSpec();
                mdseSpecB.setKey(specB.getKey());
                mdseSpecB.setValue(specB.getValue());
                mdseSpecB.setDeleted(SystemConstants.DELETED_NO);
            }

            SpecDTO specC = stockDTO.getSpecC();
            if (!ObjectUtils.isEmpty(specC)) {
                MdseSpec mdseSpecC = new MdseSpec();
                mdseSpecC.setKey(specC.getKey());
                mdseSpecC.setValue(specC.getValue());
                mdseSpecC.setDeleted(SystemConstants.DELETED_NO);
            }

            stock.setPrice(stockDTO.getPrice());
            stock.setTotalInventory(stockDTO.getTotalInventory());
            stock.setRemainingStock(stockDTO.getRemainingStock());
            stock.setNumber(stockDTO.getNumber());
            stock.setDeleted(SystemConstants.DELETED_NO);

            List<String> specPictureList = stockDTO.getSpecPictureList();
            List<MallPicture> mallPictureList = Lists.newArrayList();
            for (String link : specPictureList) {
                MallPicture picture = new MallPicture();
                picture.setLink(link);
                picture.setType(PictureType.SPEC);
                picture.setDeleted(SystemConstants.DELETED_NO);
                mallPictureList.add(picture);

            }
            stock.setSpecPictureList(mallPictureList);
            mdseStockList.add(stock);
        }
        mdse.setStockList(mdseStockList);

        return mdseRepository.save(mdse);
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
    @Transactional(rollbackFor = Exception.class)
    public Optional<MallMdse> findById(Long id) {
        Optional<MallMdse> mdseOptional = mdseRepository.findById(id);
        mdseOptional.ifPresent(System.out::println);
        return mdseOptional;
    }

    @Override
    public MdseInfo entity2vo(MallMdse entity) {
        return null;
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
    public Page<MallMdse> findPage(MdsePageQuery mdsePageQuery) {
        return null;
    }
}
