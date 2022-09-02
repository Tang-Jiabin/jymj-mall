package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.PictureDTO;
import com.jymj.mall.mdse.entity.MallPicture;
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
}
