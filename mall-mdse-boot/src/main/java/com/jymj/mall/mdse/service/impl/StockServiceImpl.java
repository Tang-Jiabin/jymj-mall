package com.jymj.mall.mdse.service.impl;

import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.entity.MdseStock;
import com.jymj.mall.mdse.service.StockService;
import com.jymj.mall.mdse.vo.StockInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    @Override
    public MdseStock add(StockDTO dto) {
        return null;
    }

    @Override
    public Optional<MdseStock> update(StockDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {

    }

    @Override
    public Optional<MdseStock> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public StockInfo entity2vo(MdseStock entity) {
        return null;
    }

    @Override
    public List<StockInfo> list2vo(List<MdseStock> entityList) {
        return null;
    }
}
