package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MallPicture;
import com.jymj.mall.mdse.enums.PictureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品图片
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Repository
public interface MallPictureRepository extends JpaRepository<MallPicture,Long> {
    List<MallPicture> findAllByMdseId(Long mdseId);

    List<MallPicture> findAllByMdseIdAndType(Long mdseId, PictureType type);

    List<MallPicture> findAllByStockIdIn(List<Long> stockIdList);

    List<MallPicture> findAllByCardId(Long cardId);

    List<MallPicture> findAllByCardIdAndType(Long cardId, PictureType type);
}
