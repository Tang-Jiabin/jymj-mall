package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.entity.MallPicture;
import com.jymj.mall.mdse.enums.PictureType;
import com.jymj.mall.mdse.vo.PictureInfo;

import java.util.List;

/**
 * 图片
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
public interface PictureService extends BaseService<MallPicture, PictureInfo, PictureDTO> {
    List<MallPicture> findAllByMdseId(Long mdseId);

    List<MallPicture> findAllByMdseIdAndType(Long mdseId, PictureType type);

    void delete(List<MallPicture> deletePicList);

    void addAll(List<PictureDTO> addPicList);

    List<MallPicture> findAllByStockIdIn(List<Long> stockIdList);

    void updateMdsePicture(List<PictureDTO> videoList, Long mdseId, PictureType mdseVideo);
    void updateCardPicture(List<PictureDTO> videoList, Long mdseId, PictureType mdseVideo);

    List<MallPicture> findAllByCardId(Long cardId);
}
