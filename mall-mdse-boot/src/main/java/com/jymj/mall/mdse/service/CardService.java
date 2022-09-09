package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.CardDTO;
import com.jymj.mall.mdse.dto.CardPageQuery;
import com.jymj.mall.mdse.entity.MdseCard;
import com.jymj.mall.mdse.vo.CardInfo;
import org.springframework.data.domain.Page;

/**
 * 卡
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-07
 */
public interface CardService extends BaseService<MdseCard, CardInfo, CardDTO> {
    Page<MdseCard> findPage(CardPageQuery cardPageQuery);
}
