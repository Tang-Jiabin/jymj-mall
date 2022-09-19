package com.jymj.mall.search.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-10
 */
@FeignClient(value = "mall-search", contextId = "mall-search")
public interface MdseFeignClient {

    @PostMapping("/api/v1/mdse")
    Result<MdseInfo> addMdse(@RequestBody MdseInfo mdseInfo);

}
