package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.admin.dto.AdminPageQuery;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.admin.entity.SysAdmin;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.common.web.service.BaseService;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
public interface AdminService extends BaseService<SysAdmin,AdminInfo,UpdateAdminDTO> {


    AdminAuthDTO getAuthInfoByUsername(String username);


    Optional<SysAdmin> findByMobile(String mobile);

    Page<SysAdmin> findPage(AdminPageQuery adminPageQuery);
}
