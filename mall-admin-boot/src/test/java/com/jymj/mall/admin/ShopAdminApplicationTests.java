package com.jymj.mall.admin;

import com.jymj.mall.admin.repository.SysDistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShopAdminApplicationTests {

    @Autowired
    private SysDistrictRepository districtRepository;

}
