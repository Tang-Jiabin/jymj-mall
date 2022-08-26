package com.jymj.mall.user.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

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
@Where(clause = "deleted = 0")
@Table(name = "mall_user")
public class MallUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_user_user_id_seq")
    private Integer userId;

    private String username;

}
