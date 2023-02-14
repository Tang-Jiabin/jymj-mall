package com.jymj.mall.admin.api;

import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-10
 */
@FeignClient(value = "mall-admin",contextId = "mall-admin")
public interface AdminFeignClient {

    @GetMapping("/api/v1/admin/username/{username}")
    Result<AdminAuthDTO> loadAdminByUsername(@PathVariable String username);

    @GetMapping("/api/v1/admin/mobile/{mobile}")
    Result<AdminAuthDTO> loadAdminByMobile(@PathVariable String mobile);

    @PostMapping("/api/v1/admin")
    Result<AdminInfo> add(UpdateAdminDTO adminDTO);

    @PutMapping("/api/v1/admin")
    Result<AdminInfo> updateAdmin(UpdateAdminDTO updateAdminDTO);

    @GetMapping("/api/v1/admin/id/{adminId}/info")
    Result<AdminInfo> getAdminById(@PathVariable Long adminId);

    @GetMapping("/api/v1/admin/mobile/{mobile}/info")
    Result<AdminInfo> getAdminByMobile(@PathVariable String mobile);

}
