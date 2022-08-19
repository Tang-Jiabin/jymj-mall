package com.jymj.mall.shop.service;


import com.jymj.mall.shop.dto.AddMallDTO;
import com.jymj.mall.shop.dto.MallPageQueryDTO;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.vo.MallVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 商场
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
public interface MallService {
    void add(AddMallDTO mallDTO);

    Page<MallDetails> findPage(MallPageQueryDTO mallPageQuery);

    List<MallVO> list2vo(List<MallDetails> content);
}
