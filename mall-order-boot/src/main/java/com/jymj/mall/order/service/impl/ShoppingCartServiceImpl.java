package com.jymj.mall.order.service.impl;

import java.math.BigDecimal;

import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.vo.PictureInfo;

import java.util.Date;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.mdse.vo.StockInfo;
import com.jymj.mall.order.dto.ShoppingCartMdseDTO;
import com.jymj.mall.order.dto.ShoppingCartPageQuery;
import com.jymj.mall.order.entity.ShoppingCartMdse;
import com.jymj.mall.order.repository.ShoppingCartMdseRepository;
import com.jymj.mall.order.service.ShoppingCartService;
import com.jymj.mall.order.vo.ShoppingCartMdseInfo;
import com.jymj.mall.shop.vo.ShopInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 购物车
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMdseRepository cartMdseRepository;
    private final MdseFeignClient mdseFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShoppingCartMdse add(ShoppingCartMdseDTO dto) {
        Long userId = UserUtils.getUserId();
        Long mdseId = dto.getMdseId();
        Long stockId = dto.getStockId();
        Long shopId = dto.getShopId();
        Integer quantity = dto.getQuantity();

        MdseInfoShow mdseInfoShow = MdseInfoShow.builder().stock(true).shop(true).build();

        Result<MdseInfo> mdseInfoResult = mdseFeignClient.getMdseOptionalById(mdseId, mdseInfoShow);
        if (!Result.isSuccess(mdseInfoResult)) {
            throw new BusinessException("商品不存在");
        }

        MdseInfo mdseInfo = mdseInfoResult.getData();
        if (!mdseInfo.getStatus().equals(SystemConstants.STATUS_OPEN)) {
            throw new BusinessException("商品已下架");
        }

        StockInfo stockInfo = mdseInfo.getStockList()
                .stream()
                .filter(info -> info.getStockId().equals(stockId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("库存信息错误"));

        ShopInfo shopInfo = mdseInfo.getShopInfoList()
                .stream()
                .filter(info -> info.getShopId().equals(shopId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("店铺信息错误"));

        if (quantity < mdseInfo.getStartingQuantity()) {
            throw new BusinessException("购买数量必须大于起售数量");
        }

        ShoppingCartMdse shoppingCartMdse = new ShoppingCartMdse();
        if (!ObjectUtils.isEmpty(dto.getShoppingCartId())) {
            Optional<ShoppingCartMdse> cartMdseOptional = findById(dto.getShoppingCartId());
            shoppingCartMdse = cartMdseOptional.orElseThrow(() -> new BusinessException("购物车信息错误"));
        }
        shoppingCartMdse.setMdseId(mdseId);
        shoppingCartMdse.setStockId(stockInfo.getStockId());
        shoppingCartMdse.setQuantity(quantity);
        shoppingCartMdse.setUserId(userId);
        shoppingCartMdse.setShopId(shopInfo.getShopId());
        shoppingCartMdse.setDeleted(0);
        return cartMdseRepository.save(shoppingCartMdse);

    }

    @Override
    public Optional<ShoppingCartMdse> update(ShoppingCartMdseDTO dto) {
        Long shoppingCartId = dto.getShoppingCartId();
        if (!ObjectUtils.isEmpty(shoppingCartId)) {
            ShoppingCartMdse shoppingCartMdse = add(dto);
            return Optional.of(shoppingCartMdse);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<ShoppingCartMdse> cartMdseList = cartMdseRepository.findAllById(idList);
            cartMdseRepository.deleteAll(cartMdseList);
        }
    }

    @Override
    public Optional<ShoppingCartMdse> findById(Long id) {
        return cartMdseRepository.findById(id);
    }

    @Override
    public ShoppingCartMdseInfo entity2vo(ShoppingCartMdse entity) {
        if (ObjectUtils.isEmpty(entity)) {

            MdseInfoShow mdseInfoShow = MdseInfoShow.builder().shop(true).stock(true).picture(true).build();
            Result<MdseInfo> mdseInfoResult = mdseFeignClient.getMdseOptionalById(entity.getMdseId(), mdseInfoShow);

            if (Result.isSuccess(mdseInfoResult)) {
                MdseInfo mdseInfo = mdseInfoResult.getData();
                ShoppingCartMdseInfo cartMdseInfo = new ShoppingCartMdseInfo();
                cartMdseInfo.setShoppingCartId(entity.getShoppingCartId());
                cartMdseInfo.setMdseId(mdseInfo.getMdseId());
                cartMdseInfo.setName(mdseInfo.getName());
                cartMdseInfo.setNumber(mdseInfo.getNumber());
                cartMdseInfo.setPrice(mdseInfo.getPrice());
                cartMdseInfo.setStartingQuantity(mdseInfo.getStartingQuantity());
                cartMdseInfo.setStatus(mdseInfo.getStatus());
                cartMdseInfo.setQuantity(entity.getQuantity());

                PictureInfo pictureInfo = mdseInfo.getPictureList()
                        .stream()
                        .filter(info -> info.getStockId().equals(entity.getStockId()))
                        .findFirst()
                        .orElse(ObjectUtils.isEmpty(mdseInfo.getPictureList()) ? null : mdseInfo.getPictureList().get(0));
                cartMdseInfo.setPictureInfo(pictureInfo);

                ShopInfo shopInfo = mdseInfo.getShopInfoList()
                        .stream()
                        .filter(info -> info.getShopId().equals(entity.getShopId()))
                        .findFirst()
                        .orElse(null);
                cartMdseInfo.setShopInfo(shopInfo);

                StockInfo stockInfo = mdseInfo.getStockList()
                        .stream()
                        .filter(info -> info.getStockId().equals(entity.getStockId()))
                        .findFirst()
                        .orElse(null);
                cartMdseInfo.setStockInfo(stockInfo);

                return cartMdseInfo;
            }
        }
        return null;
    }

    @Override
    public List<ShoppingCartMdseInfo> list2vo(List<ShoppingCartMdse> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShoppingCartMdse> findPage(ShoppingCartPageQuery shoppingCartPageQuery) {


        Pageable pageable = PageUtils.getPageable(shoppingCartPageQuery);

        Specification<ShoppingCartMdse> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (!ObjectUtils.isEmpty(shoppingCartPageQuery.getUserId())){
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), shoppingCartPageQuery.getUserId()));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return cartMdseRepository.findAll(spec, pageable);
    }
}