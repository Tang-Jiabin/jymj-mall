//package com.jymj.mall.mdse.repository;
//
//import com.jymj.mall.mdse.entity.ShopMdseMap;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * 商店商品关联
// *
// * @author J.Tang
// * @version 1.0
// * @email seven_tjb@163.com
// * @date 2022-09-06
// */
//@Repository
//public interface ShopMdseMapRepository extends JpaRepository<ShopMdseMap,Long> {
//    List<ShopMdseMap> findAllByMdseId(Long mdseId);
//
//    List<ShopMdseMap> findAllByShopId(Long shopId);
//
//    List<ShopMdseMap> findAllByShopIdIn(List<Long> idList);
//}
