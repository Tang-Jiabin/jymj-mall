package com.jymj.mall.mdse.service.impl;

import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.entity.MallPicture;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.repository.MallPictureRepository;
import com.jymj.mall.mdse.service.PictureService;
import com.jymj.mall.mdse.vo.PictureInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 图片
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final MallPictureRepository pictureRepository;

    private final ThreadPoolTaskExecutor executor;

    @Override
    public MallPicture add(PictureDTO dto) {

        MallPicture picture = new MallPicture();
        picture.setUrl(dto.getUrl());
        picture.setType(dto.getType());
        picture.setMdseId(dto.getMdseId());
        picture.setStockId(dto.getStockId());
        picture.setDeleted(SystemConstants.DELETED_NO);

        return pictureRepository.save(picture);
    }

    @Override
    @CacheEvict(value = {"mall-mdse:picture-info:", "mall-mdse:picture-entity:"}, key = "'picture-id:'+#dto.pictureId")
    public Optional<MallPicture> update(PictureDTO dto) {
        return Optional.empty();
    }

    @Override
    @CacheEvict(value = {"mall-mdse:picture-info:", "mall-mdse:picture-entity:"}, allEntries = true)
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallPicture> permissionList = pictureRepository.findAllById(idList);
            pictureRepository.deleteAll(permissionList);
        }
    }

    @Override
    @Cacheable(cacheNames = "mall-mdse:picture-entity:", key = "'picture-id:'+#id")
    public Optional<MallPicture> findById(Long id) {
        return pictureRepository.findById(id);
    }

    @Override
    @Cacheable(cacheNames = "mall-mdse:picture-info:", key = "'picture-id:'+#entity.pictureId")
    public PictureInfo entity2vo(MallPicture entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            return getPictureInfo(entity);
        }
        return null;

    }


    @NotNull
    private static PictureInfo getPictureInfo(MallPicture entity) {
        PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setPictureId(entity.getPictureId());
        pictureInfo.setUrl(entity.getUrl());
        pictureInfo.setType(entity.getType());
        pictureInfo.setStockId(entity.getStockId());
        return pictureInfo;
    }

    @Override
    public List<PictureInfo> list2vo(List<MallPicture> entityList) {
        List<CompletableFuture<PictureInfo>> futureList = Optional.of(entityList)
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
    public List<MallPicture> findAllByMdseId(Long mdseId) {
        return pictureRepository.findAllByMdseId(mdseId);
    }

    @Override
    public List<MallPicture> findAllByMdseIdAndType(Long mdseId, PictureType type) {
        return pictureRepository.findAllByMdseIdAndType(mdseId, type);
    }

    @Override
    public void delete(List<MallPicture> deletePicList) {
        if (!CollectionUtils.isEmpty(deletePicList)) {
            pictureRepository.deleteAll(deletePicList);
        }
    }

    @Override
    public void addAll(List<PictureDTO> addPicList) {
        addPicList.forEach(this::add);
    }

    @Override
    public List<MallPicture> findAllByStockIdIn(List<Long> stockIdList) {

        return pictureRepository.findAllByStockIdIn(stockIdList);
    }

    @Override
    public void updateMdsePicture(List<PictureDTO> dtoDataList, Long mdseId, PictureType type) {
        List<MallPicture> dbDataList = findAllByMdseIdAndType(mdseId, type);
        //需要删除的商品图片
        List<MallPicture> deletePicList = dbDataList.stream().filter(dbPic -> !dtoDataList.stream().map(PictureDTO::getUrl).collect(Collectors.toList()).contains(dbPic.getUrl())).collect(Collectors.toList());
        delete(deletePicList);
        //需要添加的商品图片
        List<PictureDTO> addPicList = dtoDataList.stream()
                .filter(pic -> !dbDataList.stream().map(MallPicture::getUrl).collect(Collectors.toList()).contains(pic.getUrl()))
                .map(pic -> {
                    pic.setMdseId(mdseId);
                    pic.setType(type);
                    return pic;
                }).collect(Collectors.toList());

        addAll(addPicList);
    }

    @Override
    public void updateCardPicture(List<PictureDTO> dtoDataList, Long mdseId, PictureType type) {
        List<MallPicture> dbDataList = findAllByCardIdAndType(mdseId, type);
        //需要删除的商品图片
        List<MallPicture> deletePicList = dbDataList.stream().filter(dbPic -> !dtoDataList.stream().map(PictureDTO::getUrl).collect(Collectors.toList()).contains(dbPic.getUrl())).collect(Collectors.toList());
        delete(deletePicList);
        //需要添加的商品图片
        List<PictureDTO> addPicList = dtoDataList.stream().filter(pic -> !dbDataList.stream().map(MallPicture::getUrl).collect(Collectors.toList()).contains(pic.getUrl())).collect(Collectors.toList());
        addAll(addPicList);
    }

    private List<MallPicture> findAllByCardIdAndType(Long cardId, PictureType type) {
        return pictureRepository.findAllByCardIdAndType(cardId, type);
    }

    @Override
    public List<MallPicture> findAllByCardId(Long cardId) {

        return pictureRepository.findAllByCardId(cardId);
    }

    @Override
    public List<MallPicture> findAllByStockId(Long stockId) {
        return pictureRepository.findAllByStockId(stockId);
    }


}
