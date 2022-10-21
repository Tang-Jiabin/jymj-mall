package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.entity.MdseSpec;
import com.jymj.mall.mdse.entity.MdseStock;
import com.jymj.mall.mdse.vo.StockInfo;

import java.util.List;
import java.util.Optional;

/**
 * 库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
public interface StockService extends BaseService<MdseStock, StockInfo, StockDTO> {
    MdseSpec addSpec(MdseSpec mdseSpec);

    List<MdseStock> findAllByMdseId(Long mdseId);

    void deleteMdseStock(List<MdseStock> deleteMdseStockList);

    Optional<MdseSpec> findSpecById(Long specId);

    MdseSpec saveSpec(MdseSpec mdseSpec);

    MdseStock save(MdseStock stock);

    void lessInventory(StockDTO stockDTO);
}
