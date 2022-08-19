package com.jymj.mall.common.web.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础类
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("逻辑删除标识 0-未删除 1-已删除")
    private Integer deleted;

    @JsonIgnore
    @CreatedDate
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonIgnore
    @LastModifiedDate
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonIgnore
    @CreatedBy
    @ApiModelProperty("创建用户id")
    private Long createUserId;

    @JsonIgnore
    @LastModifiedBy
    @ApiModelProperty("更新用户id")
    private Long updateUserId;



}
