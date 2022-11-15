package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.dto.TypeDTO;
import com.jymj.mall.mdse.entity.MdseType;
import com.jymj.mall.mdse.repository.MdseTypeRepository;
import com.jymj.mall.mdse.service.TypeService;
import com.jymj.mall.mdse.vo.TypeInfo;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.vo.MallInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {

    private final MdseTypeRepository typeRepository;
    private final MallFeignClient mallFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final ThreadPoolTaskExecutor executor;
    @Override
    public MdseType add(TypeDTO dto) {


        MdseType mdseType = new MdseType();
        mdseType.setName(dto.getName());
        mdseType.setRemarks(dto.getRemarks());
        mdseType.setMallId(dto.getMallId());
        mdseType.setDeleted(SystemConstants.DELETED_NO);

        return typeRepository.save(mdseType);
    }

    @Override
    @CacheEvict(cacheNames = {"mall-mdse:type-info:", "mall-mdse:type-entity:"}, key = "'type-id:'+#dto.typeId")
    public Optional<MdseType> update(TypeDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getTypeId())) {
            Optional<MdseType> typeOptional = typeRepository.findById(dto.getTypeId());
            if (typeOptional.isPresent()) {
                MdseType mdseType = typeOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName())) {
                    mdseType.setName(dto.getName());
                    update = true;
                }

                if (StringUtils.hasText(dto.getRemarks())) {
                    mdseType.setRemarks(dto.getRemarks());
                    update = true;
                }

                if (!ObjectUtils.isEmpty(dto.getMallId())) {

                    mdseType.setMallId(dto.getMallId());
                    update = true;
                }
                if (update) {
                    return Optional.of(typeRepository.save(mdseType));
                }

            }

        }


        return Optional.empty();
    }

    @Override
    @CacheEvict(value = {"mall-mdse:type-info:", "mall-mdse:type-entity:"}, allEntries = true)
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseType> mdseTypeList = typeRepository.findAllById(idList);
            typeRepository.deleteAll(mdseTypeList);
        }
    }

    @Override
    @Cacheable(cacheNames = "mall-mdse:type-entity:", key = "'label-id:'+#id")
    public Optional<MdseType> findById(Long id) {
        return typeRepository.findById(id);
    }

    @Override
    @Cacheable(cacheNames = "mall-mdse:type-info:", key = "'label-id:'+#entity.typeId")
    public TypeInfo entity2vo(MdseType entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            return getTypeInfo(entity);
        }
        return null;
    }



    @NotNull
    private static TypeInfo getTypeInfo(MdseType entity) {
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setTypeId(entity.getTypeId());
        typeInfo.setName(entity.getName());
        typeInfo.setRemarks(entity.getRemarks());
        typeInfo.setMallId(entity.getMallId());
        return typeInfo;
    }

    @Override
    public List<TypeInfo> list2vo(List<MdseType> entityList) {
        List<CompletableFuture<TypeInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    @Override
    public List<MdseType> findAllByAuth() {
        Long deptId = UserUtils.getDeptId();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.tree(deptId);

        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            Result<List<MallInfo>> mallInfoListResult = mallFeignClient.getMallByDeptIdIn(StringUtils.collectionToCommaDelimitedString(deptIdList));
            if (Result.isSuccess(mallInfoListResult)){
                List<MallInfo> mallInfoList = mallInfoListResult.getData();
                List<Long> mallIdList = mallInfoList.stream().map(MallInfo::getMallId).collect(Collectors.toList());
                return typeRepository.findAllByMallIdIn(mallIdList);
            }
        }
        return Lists.newArrayList();
    }

    @Override
    public List<MdseType> findAllByMallId(Long mallId) {

        if (mallId!=null){
            return typeRepository.findAllByMallId(mallId);
        }
        return typeRepository.findAll();
    }
}
