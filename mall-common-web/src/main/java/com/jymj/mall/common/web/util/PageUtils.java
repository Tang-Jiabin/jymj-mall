package com.jymj.mall.common.web.util;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import com.jymj.mall.common.web.vo.PageVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
public class PageUtils {

    public static Pageable getPageable(BasePageQueryDTO pageQuery) {
        if (getPageProperties(pageQuery) == null) {
            return PageRequest.of(pageQuery.getPage(), pageQuery.getSize());
        }
        return PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), getPageDirection(pageQuery), getPageProperties(pageQuery));
    }

    public static String getPageProperties(BasePageQueryDTO pageQuery) {
        return StringUtils.hasText(pageQuery.getProperties()) ? pageQuery.getProperties() : null;
    }

    public static Sort.Direction getPageDirection(BasePageQueryDTO pageQuery) {
        Sort.Direction direction = Sort.Direction.DESC;
        if (pageQuery.getDirection() != null && pageQuery.getDirection() == 1) {
            direction = Sort.Direction.ASC;
        }
        return direction;
    }

    public static <T> PageVO<T> toPageVO(Page page, List<T> content) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setEmpty(true);
        if (page != null) {
            pageVO.setContent(content);
            pageVO.setTotalPages(page.getTotalPages());
            pageVO.setTotalElements(page.getTotalElements());
            pageVO.setNumber(page.getNumber());
            pageVO.setSize(page.getSize());
            pageVO.setFirst(page.isFirst());
            pageVO.setLast(page.isLast());
            pageVO.setNumberOfElements(page.getNumberOfElements());
            pageVO.setEmpty(page.isEmpty());
        }
        return pageVO;
    }
}
