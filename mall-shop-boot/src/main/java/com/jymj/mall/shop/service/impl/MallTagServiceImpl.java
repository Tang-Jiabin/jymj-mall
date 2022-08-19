package com.jymj.mall.shop.service.impl;


import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.web.vo.OptionVO;
import com.jymj.mall.shop.entity.MallTag;
import com.jymj.mall.shop.repository.MallTagRepository;
import com.jymj.mall.shop.service.MallTagService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<OptionVO> listTagsOptions() {
        List<MallTag> tagList = mallTagRepository.findAllByDeleted(SystemConstants.DELETED_NO);
        List<OptionVO> optionList = Lists.newArrayList();
        Optional.ofNullable(tagList).orElse(Lists.newArrayList())
                .stream().forEach(mallTag -> {
                    OptionVO option = new OptionVO(mallTag.getTagId(),mallTag.getName());
                    optionList.add(option);
                });
        return optionList;
    }
}
