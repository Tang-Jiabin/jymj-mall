package com.jymj.mall.oauth.repository;

import com.jymj.mall.oauth.entity.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-10
 */
@Repository
public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails,Integer> {
    Optional<OauthClientDetails> findByClientId(String clientId);
}
