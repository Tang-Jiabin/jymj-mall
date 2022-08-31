package com.jymj.mall.mdse.service.impl;

import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.repository.MdseRepository;
import com.jymj.mall.mdse.service.MdseService;
import com.jymj.mall.mdse.vo.MdseInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Service
@RequiredArgsConstructor
public class MdseServiceImpl implements MdseService {

    private final MdseRepository mdseRepository;


    @Override
    public MallMdse add(MdseDTO dto) {
        return null;
    }

    @Override
    public Optional<MallMdse> update(MdseDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {

    }

    @Override
    public Optional<MallMdse> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public MdseInfo entity2vo(MallMdse entity) {
        return null;
    }

    @Override
    public List<MdseInfo> list2vo(List<MallMdse> entityList) {
        return null;
    }

    @Override
    public Page<MallMdse> findPage(MdsePageQuery mdsePageQuery) {
        return null;
    }
}
