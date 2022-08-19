package com.jymj.mall.admin.repository;


import com.jymj.mall.admin.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu,Long> {


    List<SysMenu> findAllByMenuIdInAndDeleted(List<Long> menuIdList, Integer deleted);
}
