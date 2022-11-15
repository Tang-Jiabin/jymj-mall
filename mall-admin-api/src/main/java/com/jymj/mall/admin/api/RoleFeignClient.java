package com.jymj.mall.admin.api;

import com.jymj.mall.admin.dto.AddRole;
import com.jymj.mall.admin.dto.RoleResource;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-06
 */
@FeignClient(value = "mall-admin", contextId = "mall-role")
public interface RoleFeignClient {
    @PostMapping("/api/v1/role")
    Result<RoleInfo>  addRole(@Valid @RequestBody AddRole addRole);

    @PutMapping("/api/v1/role/{roleId}/resources")
    Result updateRoleResource(@PathVariable Long roleId, @RequestBody RoleResource roleResourceForm);

    @GetMapping("/api/v1/role/ids/{ids}")
     Result<List<RoleInfo>> getRoleDetailList( @PathVariable String ids);
}
