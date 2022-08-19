package com.jymj.mall.admin.api;

import com.jymj.mall.admin.dto.AddAdminDTO;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-10
 */
@FeignClient(value = "mall-admin",contextId = "mall-admin")
public interface AdminFeignClient {

    @GetMapping("/api/v1/admin/{username}")
    Result<AdminAuthDTO> loadAdminByUsername(@PathVariable String username);

    @PostMapping("/api/v1/admin")
    Result add(AddAdminDTO adminDTO);
}
