package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.LabelDTO;
import com.jymj.mall.mdse.entity.MallMdseLabelMap;
import com.jymj.mall.mdse.entity.MdseLabel;
import com.jymj.mall.mdse.vo.LabelInfo;

import java.util.List;

/**
 * 标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
public interface LabelService extends BaseService<MdseLabel, LabelInfo, LabelDTO> {
    List<MdseLabel> findAllById(List<Long> idList);

    void addMdseLabelMap(Long mdseId, List<Long> labelList);

    List<MdseLabel> findAllByMdseId(Long mdseId);

    List<MdseLabel> findAllByAuth();

    List<MallMdseLabelMap> findMdseLabelAllByLabelId(Long labelId);

    List<MallMdseLabelMap> findMdseLabelAllByMdseId(Long mdseId);

    void deleteMdseLabel(List<MallMdseLabelMap> deleteMdseLabelMapList);

    List<MdseLabel> findAllByMallId(Long mallId);
}
