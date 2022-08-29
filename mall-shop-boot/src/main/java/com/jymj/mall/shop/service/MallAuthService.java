package com.jymj.mall.shop.service;

import com.jymj.mall.shop.dto.AddMallAuth;
import com.jymj.mall.shop.dto.UpdateMallAuth;
import com.jymj.mall.shop.entity.MallAuth;
import com.jymj.mall.shop.vo.MallAuthInfo;

import java.util.Optional;

/**
 * 商场授权
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
public interface MallAuthService {
    MallAuth addAuth(AddMallAuth mallAuth);

    void deleteAuth(Long id);

    MallAuth updateAuth(UpdateMallAuth updateMallAuth);

    Optional<MallAuth> getAuthByMallId(Long mallId);

    MallAuthInfo auth2vo(MallAuth mallAuth);
}
