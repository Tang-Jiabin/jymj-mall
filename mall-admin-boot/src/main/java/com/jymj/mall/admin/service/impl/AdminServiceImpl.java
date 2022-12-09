package com.jymj.mall.admin.service.impl;


import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.admin.dto.AdminPageQuery;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.admin.entity.*;
import com.jymj.mall.admin.repository.SysAdminRepository;
import com.jymj.mall.admin.repository.SysAdminRoleRepository;
import com.jymj.mall.admin.repository.SysRoleMenuRepository;
import com.jymj.mall.admin.service.AdminService;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.service.MenuService;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.admin.vo.MenuInfo;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.shop.api.VerifyFeignClient;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysAdminRepository adminRepository;
    private final SysAdminRoleRepository adminRoleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final DeptService deptService;
    private final MenuService menuService;
    private final VerifyFeignClient verifyFeignClient;
    private final ThreadPoolTaskExecutor executor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysAdmin add(UpdateAdminDTO adminDTO) {

        List<SysAdmin> adminList = adminRepository.findAllByUsernameOrMobile(adminDTO.getUsername(), adminDTO.getMobile());
        if (adminList != null && !adminList.isEmpty()) {
            throw new BusinessException("用户名或手机号已存在");
        }
        Long deptId = UserUtils.getDeptId();
        List<SysDept> deptList = deptService.findChildren(deptId);
        List<Long> deptIdList = deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        if (!Objects.equals(adminDTO.getDeptId(), deptId) && !deptIdList.contains(adminDTO.getDeptId())) {
            throw new BusinessException("只能添加本部门或下级部门成员");
        }

        SysAdmin admin = new SysAdmin();
        admin.setAdminId(null);
        admin.setNumber(adminDTO.getNumber());
        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword());
        admin.setNickname(adminDTO.getNickname());
        admin.setMobile(adminDTO.getMobile());
        admin.setGender(adminDTO.getGender());
        admin.setAvatar(adminDTO.getAvatar());
        admin.setEmail(adminDTO.getEmail());
        admin.setStatus(adminDTO.getStatus());
        admin.setDeptId(adminDTO.getDeptId());
        admin.setMallId(adminDTO.getMallId());

        admin.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        admin.setDeleted(SystemConstants.DELETED_NO);
        admin = adminRepository.save(admin);
        Long addAdminId = admin.getAdminId();

        List<SysAdminRole> adminRoleList = Lists.newArrayList();

        List<Long> roleIdList = adminDTO.getRoleIdList();
        for (Long roleId : roleIdList) {
            SysAdminRole adminRole = new SysAdminRole();
            adminRole.setAdminId(addAdminId);
            adminRole.setRoleId(roleId);
            adminRole.setDeleted(0);
            adminRoleList.add(adminRole);
        }

        if (!CollectionUtils.isEmpty(adminRoleList)) {
            adminRoleRepository.saveAll(adminRoleList);
        }
        return admin;
    }

    @Override
    @GlobalTransactional(name = "mall-admin-delete", rollbackFor = Exception.class)
    public void delete(String ids) {
        List<Long> adminIdList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(adminIdList)) {
            List<SysAdmin> adminList = adminRepository.findAllById(adminIdList);
            adminRepository.deleteAll(adminList);
            verifyFeignClient.deleteVerifyPersonByAdminIds(ids);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<SysAdmin> update(UpdateAdminDTO updateAdminDTO) {
        Optional<SysAdmin> adminOptional = adminRepository.findById(updateAdminDTO.getAdminId());
        SysAdmin admin = adminOptional.orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));

        if (StringUtils.hasText(updateAdminDTO.getUsername()) && !updateAdminDTO.getUsername().equals(admin.getUsername())) {
            Optional<SysAdmin> byUsername = adminRepository.findByUsername(updateAdminDTO.getUsername());
            if (byUsername.isPresent()) {
                throw new BusinessException("用户名已注册");
            }
            admin.setUsername(updateAdminDTO.getUsername());
        }
        if (StringUtils.hasText(updateAdminDTO.getMobile()) && !updateAdminDTO.getMobile().equals(admin.getMobile())) {
            Optional<SysAdmin> byUsername = adminRepository.findByMobile(updateAdminDTO.getMobile());
            if (byUsername.isPresent()) {
                throw new BusinessException("手机号已注册");
            }
            admin.setMobile(updateAdminDTO.getMobile());
        }

        if (StringUtils.hasText(updateAdminDTO.getPassword())) {
            admin.setPassword(passwordEncoder.encode(updateAdminDTO.getPassword()));
        }

        admin.setNickname(StringUtils.hasText(updateAdminDTO.getNickname()) ? updateAdminDTO.getNickname() : admin.getNickname());
        admin.setAvatar(StringUtils.hasText(updateAdminDTO.getAvatar()) ? updateAdminDTO.getAvatar() : admin.getAvatar());
        admin.setEmail(StringUtils.hasText(updateAdminDTO.getEmail()) ? updateAdminDTO.getEmail() : admin.getEmail());
        admin.setGender(updateAdminDTO.getGender() != null ? updateAdminDTO.getGender() : admin.getGender());
        admin.setStatus(updateAdminDTO.getStatus() != null ? updateAdminDTO.getStatus() : admin.getStatus());
        if (updateAdminDTO.getDeptId() != null) {
            Long deptId = UserUtils.getDeptId();
            List<SysDept> deptList = deptService.findChildren(deptId);
            List<Long> deptIdList = deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
            if (!Objects.equals(updateAdminDTO.getDeptId(), deptId) && !deptIdList.contains(updateAdminDTO.getDeptId())) {
                throw new BusinessException("只能添加本部门或下级部门成员");
            }
            admin.setDeptId(updateAdminDTO.getDeptId());
        }
        if (!ObjectUtils.isEmpty(updateAdminDTO.getRoleIdList())) {
            List<SysRole> newRoleList = roleService.findAllById(updateAdminDTO.getRoleIdList());
            List<SysRole> oldRoleList = roleService.findAllByAdminId(admin.getAdminId());
            List<SysRole> deleteRoleList = oldRoleList.stream().filter(role -> !newRoleList.contains(role)).collect(Collectors.toList());
            List<SysRole> addRoleList = newRoleList.stream().filter(role -> !oldRoleList.contains(role)).collect(Collectors.toList());

            roleService.deleteAdminRole(admin.getAdminId(), deleteRoleList);
            roleService.addAdminRole(admin.getAdminId(), addRoleList);
        }

        if (!ObjectUtils.isEmpty(updateAdminDTO.getVerifyPerson())) {
            admin.setVerifyPerson(updateAdminDTO.getVerifyPerson());
        }

        return Optional.of(adminRepository.save(admin));
    }

    @Override
    public Optional<SysAdmin> findById(Long adminId) {
        return adminRepository.findById(adminId);
    }

    @Override
    public AdminAuthDTO getAuthInfoByUsername(String username) {
        Optional<SysAdmin> adminOptional = adminRepository.findByUsernameAndDeleted(username, SystemConstants.DELETED_NO);
        AdminAuthDTO adminAuthDTO = null;
        if (adminOptional.isPresent()) {
            SysAdmin admin = adminOptional.get();
            adminAuthDTO = new AdminAuthDTO();
            adminAuthDTO.setUserId(admin.getAdminId());
            adminAuthDTO.setUsername(admin.getUsername());
            adminAuthDTO.setPassword(SecurityConstants.PASSWORD_ENCODE + admin.getPassword());
            adminAuthDTO.setStatus(admin.getStatus());
            adminAuthDTO.setDeptId(admin.getDeptId());
            List<SysAdminRole> adminRoleList = adminRoleRepository.findAllByAdminId(admin.getAdminId());
            List<Long> roleIdList = adminRoleList.stream().map(SysAdminRole::getRoleId).collect(Collectors.toList());
            List<SysRole> roleList = roleService.findAllById(roleIdList);
            List<String> roleStrList = Lists.newArrayList();
            roleList.forEach(sysRole -> roleStrList.add(sysRole.getCode()));
            adminAuthDTO.setRoles(roleStrList);
        }

        return adminAuthDTO;
    }


    @Override
    public AdminInfo entity2vo(SysAdmin admin) {
        if (!ObjectUtils.isEmpty(admin)) {
            AdminInfo adminInfo = new AdminInfo();
            adminInfo.setAdminId(admin.getAdminId());
            adminInfo.setUsername(admin.getUsername());
            adminInfo.setNickname(admin.getNickname());
            adminInfo.setMobile(admin.getMobile());
            adminInfo.setGender(admin.getGender());
            adminInfo.setAvatar(admin.getAvatar());
            adminInfo.setEmail(admin.getEmail());
            adminInfo.setMallId(admin.getMallId());
            adminInfo.setStatus(admin.getStatus());
            adminInfo.setNumber(admin.getNumber());
            adminInfo.setOperationTime(admin.getUpdateTime());
            adminInfo.setVerifyPerson(admin.getVerifyPerson());

            Optional<SysAdmin> adminOptional = findById(admin.getUpdateUserId());
            adminOptional.ifPresent(operator -> adminInfo.setOperator(operator.getNickname()));

            List<SysRole> roleList = roleService.findAllByAdminId(admin.getAdminId());
            List<RoleInfo> roleInfoList = roleService.list2vo(roleList);
            adminInfo.setRoleInfoList(roleInfoList);

            Optional<SysDept> deptOptional = deptService.findById(admin.getDeptId());
            deptOptional.ifPresent(dept -> adminInfo.setDeptInfo(deptService.entity2vo(dept)));

            List<SysMenu> menuList = menuService.findAllByRoleIdIn(roleList.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            List<MenuInfo> menuInfoList = menuService.list2tree(menuList, SystemConstants.ROOT_MENU_ID);
            adminInfo.setMenuInfoList(menuInfoList);

            return adminInfo;
        }
        return null;
    }

    @Override
    public List<AdminInfo> list2vo(List<SysAdmin> entityList) {
        List<CompletableFuture<AdminInfo>> futureList = Optional.of(entityList)
                .orElse(org.assertj.core.util.Lists.newArrayList())
                .stream().filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor)).collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<SysAdmin> findByMobile(String mobile) {
        return adminRepository.findByMobile(mobile);
    }

    @Override
    public Page<SysAdmin> findPage(AdminPageQuery adminPageQuery) {

        Pageable pageable = PageUtils.getPageable(adminPageQuery);

        Long deptId = UserUtils.getDeptId();
        List<SysDept> deptList = deptService.findChildren(deptId);
        deptList.add(SysDept.builder().deptId(0L).build());
        List<Long> deptIdList = deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        deptIdList.add(deptId);


        Specification<SysAdmin> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (StringUtils.hasText(adminPageQuery.getNumber())) {
                list.add(criteriaBuilder.like(root.get("number").as(String.class), SystemConstants.generateSqlLike(adminPageQuery.getNumber())));
            }

            if (StringUtils.hasText(adminPageQuery.getNickname())) {
                list.add(criteriaBuilder.like(root.get("nickname").as(String.class), SystemConstants.generateSqlLike(adminPageQuery.getNickname() )));
            }

            if (StringUtils.hasText(adminPageQuery.getMobile())) {
                list.add(criteriaBuilder.like(root.get("mobile").as(String.class), SystemConstants.generateSqlLike(adminPageQuery.getMobile())));
            }

            if (Objects.nonNull(adminPageQuery.getMallId())) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Long.class), adminPageQuery.getMallId()));
            }

            if (!ObjectUtils.isEmpty(adminPageQuery.getRoleId())) {
                List<SysAdminRole> adminRoleList = roleService.findAdminRoleAllByRoleId(adminPageQuery.getRoleId());
                List<Long> adminIdList = adminRoleList.stream().map(SysAdminRole::getAdminId).collect(Collectors.toList());
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("adminId").as(Long.class));
                adminIdList.forEach(in::value);
                list.add(in);
            }

            CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("deptId").as(Long.class));
            deptIdList.forEach(in::value);
            list.add(in);

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return adminRepository.findAll(spec, pageable);
    }
}
