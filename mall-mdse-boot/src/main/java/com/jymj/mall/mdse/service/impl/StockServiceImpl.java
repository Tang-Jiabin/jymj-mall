package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.redis.RedissonLockUtil;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.dto.SpecDTO;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.entity.MallPicture;
import com.jymj.mall.mdse.entity.MdseSpec;
import com.jymj.mall.mdse.entity.MdseStock;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.MdseSpecRepository;
import com.jymj.mall.mdse.repository.MdseStockRepository;
import com.jymj.mall.mdse.service.PictureService;
import com.jymj.mall.mdse.service.StockService;
import com.jymj.mall.mdse.vo.PictureInfo;
import com.jymj.mall.mdse.vo.SpecInfo;
import com.jymj.mall.mdse.vo.StockInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final PictureService pictureService;
    private final RedissonLockUtil redissonLockUtil;
    private final MdseSpecRepository mdseSpecRepository;
    private final ThreadPoolTaskExecutor executor;
    private final MdseStockRepository stockRepository;

    @Override
    public MdseStock add(StockDTO dto) {

        MdseStock stock = new MdseStock();

        SpecDTO specA = dto.getSpecA();
        stock.setSpecA(getMdseSpecId(dto, specA));

        SpecDTO specB = dto.getSpecB();
        stock.setSpecB(getMdseSpecId(dto, specB));

        SpecDTO specC = dto.getSpecC();
        stock.setSpecC(getMdseSpecId(dto, specC));

        stock.setMdseId(dto.getMdseId());
        stock.setPrice(dto.getPrice());
        stock.setTotalInventory(dto.getTotalInventory());
        stock.setRemainingStock(dto.getTotalInventory());
        stock.setNumber(dto.getNumber());
        stock.setDeleted(SystemConstants.DELETED_NO);

        stock = stockRepository.save(stock);
        List<PictureDTO> specPictureList = dto.getSpecPictureList();

        for (PictureDTO pictureDTO : specPictureList) {
            PictureDTO picture = new PictureDTO();
            picture.setUrl(pictureDTO.getUrl());
            picture.setMdseId(dto.getMdseId());
            picture.setStockId(stock.getStockId());
            picture.setType(PictureType.STOCK_SPEC);
            pictureService.add(picture);

        }

        return stock;
    }

    @Override
    public Optional<MdseStock> update(StockDTO dto) {
        Optional<MdseStock> stockOptional = stockRepository.findById(dto.getStockId());
        if (stockOptional.isPresent()) {
            MdseStock stock = stockOptional.get();
            boolean update = false;

            if (dto.getMdseId() != null && !Objects.equals(dto.getMdseId(), stock.getMdseId())) {
                stock.setMdseId(dto.getMdseId());
                update = true;
            }

            if (dto.getPrice() != null && !Objects.equals(dto.getPrice(), stock.getPrice())) {
                stock.setPrice(dto.getPrice());
                update = true;
            }

            if (dto.getTotalInventory() != null && !Objects.equals(dto.getTotalInventory(), stock.getRemainingStock())) {
                int totalInventory = ~(stock.getRemainingStock() - dto.getTotalInventory()-1) + stock.getTotalInventory();
                stock.setRemainingStock(dto.getTotalInventory());
                stock.setTotalInventory(totalInventory);
                update = true;
            }

            if (StringUtils.hasText(dto.getNumber()) && !Objects.equals(dto.getNumber(), stock.getNumber())) {
                stock.setNumber(dto.getNumber());
                update = true;
            }

            MdseSpec mdseSpecA = updateStockSpec(stock.getMdseId(), dto.getSpecA());
            if (!ObjectUtils.isEmpty(mdseSpecA)) {
                stock.setSpecA(mdseSpecA.getSpecId());
                update = true;
            }

            MdseSpec mdseSpecB = updateStockSpec(stock.getMdseId(), dto.getSpecB());
            if (!ObjectUtils.isEmpty(mdseSpecB)) {
                stock.setSpecB(mdseSpecB.getSpecId());
                update = true;
            }

            MdseSpec mdseSpecC = updateStockSpec(stock.getMdseId(), dto.getSpecC());
            if (!ObjectUtils.isEmpty(mdseSpecC)) {
                stock.setSpecC(mdseSpecC.getSpecId());
                update = true;
            }

            List<PictureDTO> specPictureList = dto.getSpecPictureList();
            if (!ObjectUtils.isEmpty(specPictureList)) {
                pictureService.updateMdsePicture(specPictureList, dto.getMdseId(), PictureType.STOCK_SPEC);
            }

            if (update) {
                return Optional.of(save(stock));
            }
        }

        return Optional.empty();
    }

    private Long getMdseSpecId(StockDTO dto, SpecDTO specA) {
        MdseSpec mdseSpec = null;
        if (!ObjectUtils.isEmpty(specA)) {
            MdseSpec mdseSpecA = new MdseSpec();
            mdseSpecA.setMdseId(dto.getMdseId());
            mdseSpecA.setKey(specA.getKey());
            mdseSpecA.setValue(specA.getValue());
            mdseSpecA.setDeleted(SystemConstants.DELETED_NO);
            if (specA.getSpecId() != null) {
                Optional<MdseSpec> mdseSpecOptional = mdseSpecRepository.findById(specA.getSpecId());
                if (mdseSpecOptional.isPresent()) {

                }
            } else {
                mdseSpec = addSpec(mdseSpecA);
            }
            return mdseSpec.getSpecId();
        }
        return null;
    }

    private MdseSpec updateStockSpec(Long mdseId, SpecDTO specA) {
        if (!ObjectUtils.isEmpty(specA)) {
            if (ObjectUtils.isEmpty(specA.getSpecId()) && StringUtils.hasText(specA.getKey()) && StringUtils.hasText(specA.getValue())) {
                MdseSpec mdseSpec = new MdseSpec();
                mdseSpec.setKey(specA.getKey());
                mdseSpec.setValue(specA.getValue());
                mdseSpec.setMdseId(mdseId);
                mdseSpec.setDeleted(SystemConstants.DELETED_NO);
                mdseSpec = addSpec(mdseSpec);
                return mdseSpec;
            } else {
                Optional<MdseSpec> specOptional = findSpecById(specA.getSpecId());
                if (specOptional.isPresent()) {
                    MdseSpec mdseSpec = specOptional.get();
                    if (StringUtils.hasText(specA.getKey()) && StringUtils.hasText(specA.getValue())) {
                        mdseSpec.setKey(specA.getKey());
                        mdseSpec.setValue(specA.getValue());
                        saveSpec(mdseSpec);
                    }
                    return mdseSpec;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseStock> brandList = stockRepository.findAllById(idList);
            stockRepository.deleteAll(brandList);
        }
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
        List<MallPicture> pictureList = pictureService.findAllByStockId(entity.getStockId());
        List<PictureInfo> pictureInfoList = pictureService.list2vo(pictureList);
        stockInfo.setPictureList(pictureInfoList);
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
        List<CompletableFuture<StockInfo>> futureList = Optional.of(entityList)
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
    public MdseSpec addSpec(MdseSpec mdseSpec) {

        return mdseSpecRepository.save(mdseSpec);

    }

    @Override
    public List<MdseStock> findAllByMdseId(Long mdseId) {

        return stockRepository.findAllByMdseId(mdseId);
    }

    @Override
    public void deleteMdseStock(List<MdseStock> deleteMdseStockList) {
        List<Long> stockIdList = deleteMdseStockList.stream().map(MdseStock::getStockId).collect(Collectors.toList());
        delete(StringUtils.collectionToCommaDelimitedString(stockIdList));
        List<MallPicture> pictureList = pictureService.findAllByStockIdIn(stockIdList);
        pictureService.delete(pictureList);
    }

    @Override
    public Optional<MdseSpec> findSpecById(Long specId) {
        return mdseSpecRepository.findById(specId);
    }

    @Override
    public MdseSpec saveSpec(MdseSpec mdseSpec) {
        return mdseSpecRepository.save(mdseSpec);
    }

    @Override
    public MdseStock save(MdseStock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public void lessInventory(StockDTO stockDTO) {
        Long stockId = stockDTO.getStockId();
        String key = String.format("mall-mdse:stock:less:%d",stockId);
        Boolean lock = redissonLockUtil.lock(key);
        if (Boolean.TRUE.equals(lock)){
            Integer inventory = stockDTO.getTotalInventory();
            if (ObjectUtils.isEmpty(stockId) || ObjectUtils.isEmpty(inventory)) {
                redissonLockUtil.unlock(key);
                throw new BusinessException("参数错误");
            }
            Optional<MdseStock> mdseStock = stockRepository.findById(stockId);
            mdseStock.ifPresent(stock -> {
                if (stock.getRemainingStock() - inventory < 0) {
                    redissonLockUtil.unlock(key);
                    throw new BusinessException("库存不足");
                }
                stock.setRemainingStock(stock.getRemainingStock() - inventory);
                stockRepository.save(stock);
            });
            redissonLockUtil.unlock(key);
        }
    }

    @Override
    public List<MdseStock> findAllByRemainingStockGreaterThanOrEqual(Integer quantityGreaterThanOrEqual) {
        return stockRepository.findAllByRemainingStockGreaterThanEqual(quantityGreaterThanOrEqual);
    }

    @Override
    public List<MdseStock> findAllByRemainingStockLessThanEqual(Integer quantityLessThanOrEqual) {
        return stockRepository.findAllByRemainingStockLessThanEqual(quantityLessThanOrEqual);
    }
}
