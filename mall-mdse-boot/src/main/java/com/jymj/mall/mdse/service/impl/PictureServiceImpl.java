package com.jymj.mall.mdse.service.impl;

import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.entity.MallPicture;
import com.jymj.mall.mdse.repository.MallPictureRepository;
import com.jymj.mall.mdse.service.PictureService;
import com.jymj.mall.mdse.vo.PictureInfo;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 图片
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final MallPictureRepository pictureRepository;

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
    public Optional<MallPicture> update(PictureDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {

    }

    @Override
    public Optional<MallPicture> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public PictureInfo entity2vo(MallPicture entity) {
        PictureInfo pictureInfo = new PictureInfo();
        pictureInfo.setPictureId(entity.getPictureId());
        pictureInfo.setUrl(entity.getUrl());
        pictureInfo.setType(entity.getType());
        pictureInfo.setStockId(entity.getStockId());
        return pictureInfo;
    }

    @Override
    public List<PictureInfo> list2vo(List<MallPicture> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public List<MallPicture> findAllByMdseId(Long mdseId) {
        return pictureRepository.findAllByMdseId(mdseId);
    }
}
