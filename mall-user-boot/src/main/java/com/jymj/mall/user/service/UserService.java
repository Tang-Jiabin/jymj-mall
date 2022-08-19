package com.jymj.mall.user.service;


import com.jymj.mall.admin.dto.AdminAuthDTO;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
public interface UserService {
    void findUserById(int i);

    AdminAuthDTO findUserByUsername(String username);
}
