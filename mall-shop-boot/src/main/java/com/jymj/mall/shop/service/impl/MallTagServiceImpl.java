package com.jymj.mall.shop.service.impl;

import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.web.vo.OptionVO;
import com.jymj.mall.shop.entity.MallDetailsTag;
import com.jymj.mall.shop.entity.MallTag;
import com.jymj.mall.shop.repository.MallDetailsTagRepository;
import com.jymj.mall.shop.repository.MallTagRepository;
import com.jymj.mall.shop.service.MallTagService;
import com.jymj.mall.shop.vo.TagInfo;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-16
 */
@Service
@RequiredArgsConstructor
public class MallTagServiceImpl implements MallTagService {

    private final MallTagRepository mallTagRepository;
    private final MallDetailsTagRepository mallDetailsTagRepository;

    @Override
    public List<OptionVO> listTagsOptions() {
        List<MallTag> tagList = mallTagRepository.findAllByDeleted(SystemConstants.DELETED_NO);
        List<OptionVO> optionList = Lists.newArrayList();
        Optional.ofNullable(tagList).orElse(Lists.newArrayList())
                .stream().forEach(mallTag -> {
                    OptionVO option = new OptionVO(mallTag.getTagId(), mallTag.getName());
                    optionList.add(option);
                });
        return optionList;
    }

    @Override
    public void addMallTag(Long mdId, List<Long> tagIdList) {
        List<MallDetailsTag> mallDetailsTagList = Lists.newArrayList();
        for (Long tagId : tagIdList) {
            MallDetailsTag mallDetailsTag = new MallDetailsTag();
            mallDetailsTag.setMallId(mdId);
            mallDetailsTag.setTagId(tagId);
            mallDetailsTag.setDeleted(SystemConstants.DELETED_NO);
            mallDetailsTagList.add(mallDetailsTag);
        }
        mallDetailsTagRepository.saveAll(mallDetailsTagList);
    }

    @Override
    public List<MallTag> findAllByMallId(Long mallId) {
        List<MallDetailsTag> mallDetailsTagList = mallDetailsTagRepository.findAllByMallId(mallId);
        List<Long> tagIdList = mallDetailsTagList.stream().map(MallDetailsTag::getTagId).collect(Collectors.toList());
        return mallTagRepository.findAllById(tagIdList);
    }

    @Override
    public List<Long> findAllTagIdByMallId(Long mallId) {
        List<MallDetailsTag> mallDetailsTagList = mallDetailsTagRepository.findAllByMallId(mallId);
        return mallDetailsTagList.stream().map(MallDetailsTag::getTagId).collect(Collectors.toList());
    }

    @Override
    public void deleteMallTag(Long mallId, List<Long> deleteIdList) {
        mallDetailsTagRepository.deleteAllByMallIdAndTagIdIn(mallId, deleteIdList);
    }

    @Override
    public List<TagInfo> list2vo(List<MallTag> mallTagList) {
        List<TagInfo> tagInfoList = Lists.newArrayList();
        Optional.ofNullable(mallTagList).orElse(Lists.newArrayList()).forEach(mallTag -> {
            TagInfo tagInfo = tag2vo(mallTag);
            tagInfoList.add(tagInfo);
        });
        return tagInfoList;
    }

    @NotNull
    private static TagInfo tag2vo(MallTag mallTag) {
        TagInfo tagInfo = new TagInfo();
        tagInfo.setTagId(mallTag.getTagId());
        tagInfo.setName(mallTag.getName());
        return tagInfo;
    }
}
