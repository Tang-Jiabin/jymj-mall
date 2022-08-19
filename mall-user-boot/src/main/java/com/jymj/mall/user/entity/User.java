package com.jymj.mall.user.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 用户
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Data
@Entity
@Table(name = "mall_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

}
