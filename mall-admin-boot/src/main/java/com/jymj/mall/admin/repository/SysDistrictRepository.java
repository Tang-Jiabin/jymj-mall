package com.jymj.mall.admin.repository;

import com.jymj.mall.admin.entity.SysDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Repository
public interface SysDistrictRepository extends JpaRepository<SysDistrict,Long> {
    List<SysDistrict> findAllByDeleted(Integer deleted);

    List<SysDistrict> findAllByPid(Long districtId);


    List<SysDistrict> findAllByTreePathIsLikeAndDeleted(String treePath,Integer deleted);

    Optional<SysDistrict> findByDistrictIdAndDeleted(Long districtId, Integer deleted);
}
