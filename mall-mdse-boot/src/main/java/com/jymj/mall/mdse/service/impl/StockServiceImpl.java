package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.dto.SpecDTO;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.entity.MdseSpec;
import com.jymj.mall.mdse.entity.MdseStock;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.MdseSpecRepository;
import com.jymj.mall.mdse.repository.MdseStockRepository;
import com.jymj.mall.mdse.service.PictureService;
import com.jymj.mall.mdse.service.StockService;
import com.jymj.mall.mdse.vo.SpecInfo;
import com.jymj.mall.mdse.vo.StockInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final PictureService pictureService;

    private final MdseSpecRepository mdseSpecRepository;

    private final MdseStockRepository stockRepository;

    @Override
    public MdseStock add(StockDTO dto) {

        MdseStock stock = new MdseStock();

        SpecDTO specA = dto.getSpecA();
        if (!ObjectUtils.isEmpty(specA)) {
            MdseSpec mdseSpecA = new MdseSpec();
            mdseSpecA.setKey(specA.getKey());
            mdseSpecA.setValue(specA.getValue());
            mdseSpecA.setDeleted(SystemConstants.DELETED_NO);
            mdseSpecA = addSpec(mdseSpecA);
            stock.setSpecA(mdseSpecA.getSpecId());
        }


        SpecDTO specB = dto.getSpecB();
        if (!ObjectUtils.isEmpty(specB)) {
            MdseSpec mdseSpecB = new MdseSpec();
            mdseSpecB.setKey(specB.getKey());
            mdseSpecB.setValue(specB.getValue());
            mdseSpecB.setDeleted(SystemConstants.DELETED_NO);
            mdseSpecB = addSpec(mdseSpecB);
            stock.setSpecB(mdseSpecB.getSpecId());
        }

        SpecDTO specC = dto.getSpecC();
        if (!ObjectUtils.isEmpty(specC)) {
            MdseSpec mdseSpecC = new MdseSpec();
            mdseSpecC.setKey(specC.getKey());
            mdseSpecC.setValue(specC.getValue());
            mdseSpecC.setDeleted(SystemConstants.DELETED_NO);
            mdseSpecC = addSpec(mdseSpecC);
            stock.setSpecC(mdseSpecC.getSpecId());
        }

        stock.setMdseId(dto.getMdseId());
        stock.setPrice(dto.getPrice());
        stock.setTotalInventory(dto.getTotalInventory());
        stock.setRemainingStock(dto.getRemainingStock());
        stock.setNumber(dto.getNumber());
        stock.setDeleted(SystemConstants.DELETED_NO);


        List<String> specPictureList = dto.getSpecPictureList();

        for (String url : specPictureList) {
            PictureDTO picture = new PictureDTO();
            picture.setUrl(url);
            picture.setMdseId(dto.getMdseId());
            picture.setStockId(stock.getStockId());
            picture.setType(PictureType.STOCK_SPEC);
            pictureService.add(picture);

        }

        return stockRepository.save(stock);
    }

    @Override
    public Optional<MdseStock> update(StockDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {

    }

    @Override
    public Optional<MdseStock> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public StockInfo entity2vo(MdseStock entity) {
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockId(entity.getStockId());
        stockInfo.setSpecA(spec2vo(entity.getSpecA()));
        stockInfo.setSpecB(spec2vo(entity.getSpecB()));
        stockInfo.setSpecC(spec2vo(entity.getSpecC()));
        stockInfo.setPrice(entity.getPrice());
        stockInfo.setTotalInventory(entity.getTotalInventory());
        stockInfo.setRemainingStock(entity.getRemainingStock());
        stockInfo.setNumber(entity.getNumber());

        return stockInfo;
    }

    private SpecInfo spec2vo(Long specId) {
        if (!ObjectUtils.isEmpty(specId)) {
            Optional<MdseSpec> specOptional = mdseSpecRepository.findById(specId);
            if (specOptional.isPresent()) {
                SpecInfo specInfo = new SpecInfo();
                specInfo.setSpecId(specOptional.get().getSpecId());
                specInfo.setKey(specOptional.get().getKey());
                specInfo.setValue(specOptional.get().getValue());
                return specInfo;
            }
        }
        return null;
    }

    @Override
    public List<StockInfo> list2vo(List<MdseStock> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public MdseSpec addSpec(MdseSpec mdseSpec) {

        return mdseSpecRepository.save(mdseSpec);

    }

    @Override
    public List<MdseStock> findAllByMdseId(Long mdseId) {

        return stockRepository.findAllByMdseId(mdseId);
    }
}
