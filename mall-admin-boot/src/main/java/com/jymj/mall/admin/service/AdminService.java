package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.AddAdminDTO;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.admin.entity.SysAdmin;
import com.jymj.mall.admin.vo.AdminInfo;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
public interface AdminService {
    void add(AddAdminDTO adminDTO);

    AdminAuthDTO getAuthInfoByUsername(String username);

    Optional<SysAdmin> findById(Long adminId);

    AdminInfo admin2vo(SysAdmin admin);

    void deleteAdmin(String ids);

    void updateAdmin(UpdateAdminDTO updateAdminDTO);

    Optional<SysAdmin> findByMobile(String mobile);
}
