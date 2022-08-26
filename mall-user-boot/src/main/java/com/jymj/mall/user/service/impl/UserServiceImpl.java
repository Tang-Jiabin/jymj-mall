package com.jymj.mall.user.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.user.entity.MallUser;
import com.jymj.mall.user.repository.UserRepository;
import com.jymj.mall.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void findUserById(int id) {
        Optional<MallUser> userOptional = userRepository.findById(id);

    }

    @Override
    public AdminAuthDTO findUserByUsername(String username) {

        Optional<MallUser> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            MallUser user = userOptional.get();
            AdminAuthDTO adminAuthDTO = new AdminAuthDTO();
            adminAuthDTO.setUserId(1L);
            adminAuthDTO.setUsername("admin");
            adminAuthDTO.setPassword("123456");
            adminAuthDTO.setStatus(1);
            adminAuthDTO.setRoles(Lists.newArrayList("ROOT","ADMIN"));
            adminAuthDTO.setDeptId(1L);

            return adminAuthDTO;
        }
        throw new  RuntimeException();
    }
}
