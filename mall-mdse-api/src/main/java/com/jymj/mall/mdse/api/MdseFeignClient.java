package com.jymj.mall.mdse.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品Client
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-12
 */
@FeignClient(value = "mall-mdse",contextId = "mall-mdse")
public interface MdseFeignClient {

    @GetMapping("/api/v1/mdse/{mdseId}/optional")
    Result<MdseInfo> getMdseOptionalById(@Valid @PathVariable Long mdseId,@SpringQueryMap MdseInfoShow show);

    @GetMapping("/api/v1/mdse/all/optional/{ids}")
    Result<List<MdseInfo>> getAllMdseOptionalByIds(@Valid @PathVariable String ids, @SpringQueryMap MdseInfoShow show);
}
