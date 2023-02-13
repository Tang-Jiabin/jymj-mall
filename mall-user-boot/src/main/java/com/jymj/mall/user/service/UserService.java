package com.jymj.mall.user.service;


import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.dto.UserPageQuery;
import com.jymj.mall.user.entity.MallUser;
import com.jymj.mall.user.vo.UserInfo;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
public interface UserService extends BaseService<MallUser, UserInfo, UserDTO> {


    Optional<UserAuthDTO> loadUserByOpenid(String openid);

    Page<MallUser> findPage(UserPageQuery userPageQuery);


    Optional<UserAuthDTO> loadUserByUsername(String username);

}
