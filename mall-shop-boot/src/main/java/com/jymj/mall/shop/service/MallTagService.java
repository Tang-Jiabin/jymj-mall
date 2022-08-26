package com.jymj.mall.shop.service;


import com.jymj.mall.common.web.vo.OptionVO;
import com.jymj.mall.shop.entity.MallTag;
import com.jymj.mall.shop.vo.TagInfo;

import java.util.List;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-16
 */
public interface MallTagService {
    List<OptionVO> listTagsOptions();

    void addMallTag(Long mdId, List<Long> tagId);

    List<MallTag> findAllByMallId(Long mallId);

    List<Long> findAllTagIdByMallId(Long mallId);

    void deleteMallTag(Long mallId, List<Long> deleteIdList);

    List<TagInfo> list2vo(List<MallTag> mallTagList);
}
