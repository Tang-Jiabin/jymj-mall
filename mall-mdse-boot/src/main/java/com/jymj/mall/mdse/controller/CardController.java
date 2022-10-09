package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.CardDTO;
import com.jymj.mall.mdse.dto.CardPageQuery;
import com.jymj.mall.mdse.entity.MdseCard;
import com.jymj.mall.mdse.service.CardService;
import com.jymj.mall.mdse.vo.CardInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 卡
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-07
 */
@Api(tags = "卡")
@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @ApiOperation(value = "添加卡")
    @PostMapping
    public Result<CardInfo> addCard(@Valid @RequestBody CardDTO cardDTO) {
        MdseCard card = cardService.add(cardDTO);
        CardInfo cardInfo = cardService.entity2vo(card);
        cardService.syncToElasticAddCardInfo(cardInfo);
        return Result.success(cardInfo);
    }

    @ApiOperation(value = "删除卡")
    @DeleteMapping("/{ids}")
    public Result<String> deleteCard(@Valid @PathVariable String ids) {
        cardService.delete(ids);
        cardService.syncToElasticDeleteCardInfo(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改卡")
    @PutMapping
    public Result<CardInfo> updateCard(@RequestBody CardDTO cardDTO) {
        Optional<MdseCard> cardOptional = cardService.update(cardDTO);
        if (cardOptional.isPresent()) {
            CardInfo cardInfo = cardService.entity2vo(cardOptional.get());
            cardService.syncToElasticUpdateCardInfo(cardInfo);
            return Result.success(cardInfo);
        }
        return Result.failed("更新失败");
    }

    @ApiOperation(value = "卡信息")
    @GetMapping("/{cardId}/info")
    public Result<CardInfo> getCardById(@Valid @PathVariable Long cardId) {
        Optional<MdseCard> cardOptional = cardService.findById(cardId);
        return cardOptional.map(entity -> Result.success(cardService.entity2vo(entity))).orElse(Result.failed("卡信息不存在"));
    }

    @ApiOperation(value = "卡分页")
    @GetMapping("/pages")
    public Result<PageVO<CardInfo>> pages(CardPageQuery cardPageQuery) {
        Page<MdseCard> page = cardService.findPage(cardPageQuery);
        List<CardInfo> cardInfoList = cardService.list2vo(page.getContent());
        PageVO<CardInfo> pageVo = PageUtils.toPageVO(page, cardInfoList);
        return Result.success(pageVo);
    }

}
