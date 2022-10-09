package com.jymj.mall.search.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.CardPageQuery;
import com.jymj.mall.mdse.vo.CardInfo;
import com.jymj.mall.search.service.CardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 卡
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-20
 */
@Api(tags = "卡")
@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardController {


    private final CardService cardService;

    @ApiOperation(value = "添加卡")
    @PostMapping
    public Result<CardInfo> addCard(@Valid @RequestBody CardInfo cardInfo) {
        cardInfo = cardService.add(cardInfo);
        return Result.success(cardInfo);
    }

    @ApiOperation(value = "删除卡")
    @DeleteMapping("/{ids}")
    public Result<String> deleteCard(@Valid @PathVariable String ids) {
        cardService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改卡")
    @PutMapping
    public Result<CardInfo> updateCard(@RequestBody CardInfo cardInfo) {
        cardInfo = cardService.update(cardInfo);

        return Result.success(cardInfo);
    }

    @ApiOperation(value = "卡信息")
    @GetMapping("/{cardId}/info")
    public Result<CardInfo> getCardById(@Valid @PathVariable Long cardId) {
        Optional<CardInfo> cardOptional = cardService.findById(cardId);
        return cardOptional.map(Result::success).orElse(Result.failed("卡信息不存在"));
    }

    @ApiOperation(value = "卡分页")
    @GetMapping("/pages")
    public Result<PageVO<CardInfo>> pages(CardPageQuery cardPageQuery) {
        SearchPage<CardInfo> page = cardService.findPage(cardPageQuery);
        List<CardInfo> content = page.getContent().stream().map(SearchHit::getContent).collect(Collectors.toList());
        PageVO<CardInfo> pageVo = PageUtils.toPageVO(page, content);
        return Result.success(pageVo);
    }
}
