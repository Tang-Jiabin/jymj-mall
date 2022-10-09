package com.jymj.mall.search.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.vo.CardInfo;
import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-10
 */
@FeignClient(value = "mall-search", contextId = "mall-search")
public interface MdseInfoFeignClient {

    @PostMapping("/api/v1/mdse")
    Result<MdseInfo> addMdse(@RequestBody MdseInfo mdseInfo);

    @PutMapping("/api/v1/mdse")
    void updateMdse(@RequestBody MdseInfo mdseInfo);

    @DeleteMapping("/api/v1/mdse/{ids}")
    void deleteMdse(@PathVariable String ids);

    @PostMapping("/api/v1/mdse")
    void addCard(@RequestBody CardInfo cardInfo);

    @DeleteMapping("/api/v1/mdse/{ids}")
    void deleteCard(String ids);

    @PutMapping("/api/v1/mdse")
    void updateCard( @RequestBody CardInfo cardInfo);

    @PutMapping("/api/v1/mdse/list")
    void updateMdseList( @RequestBody List<MdseInfo> mdseInfoList);
}
