package com.jymj.mall.oauth.security.core.clientdetails;


import com.jymj.mall.common.enums.PasswordEncoderTypeEnum;
import com.jymj.mall.oauth.dto.ClientAuthDTO;
import com.jymj.mall.oauth.entity.OauthClientDetails;
import com.jymj.mall.oauth.repository.OauthClientDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * 客户端信息
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private final OauthClientDetailsRepository oauthClientDetailsRepository;

    @Override
    @Cacheable(cacheNames = "auth", key = "'oauth-client:'+#clientId")
    public ClientDetails loadClientByClientId(String clientId) {
        try {
            Optional<OauthClientDetails> oauthClientDetails = oauthClientDetailsRepository.findByClientId(clientId);
            log.info("loadClient:{}",oauthClientDetails.toString());
            if (oauthClientDetails.isPresent()) {
                ClientAuthDTO client = client2dto(oauthClientDetails.get());
                BaseClientDetails clientDetails = new BaseClientDetails(
                        client.getClientId(),
                        client.getResourceIds(),
                        client.getScope(),
                        client.getAuthorizedGrantTypes(),
                        client.getAuthorities(),
                        client.getWebServerRedirectUri()
                );
                clientDetails.setClientSecret(PasswordEncoderTypeEnum.NOOP.getPrefix() + client.getClientSecret());
                clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
                clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
                return clientDetails;
            }else {
                throw new NoSuchClientException("No client with requested id: " + clientId);
            }
            
        } catch (Exception var4) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }

    private ClientAuthDTO client2dto(OauthClientDetails oauthClientDetails) {
        ClientAuthDTO clientAuthDTO = new ClientAuthDTO();
        clientAuthDTO.setClientId(oauthClientDetails.getClientId());
        clientAuthDTO.setClientSecret(oauthClientDetails.getClientSecret());
        clientAuthDTO.setResourceIds(oauthClientDetails.getResourceIds());
        clientAuthDTO.setScope(oauthClientDetails.getScope());
        clientAuthDTO.setAuthorizedGrantTypes(oauthClientDetails.getAuthorizedGrantTypes());
        clientAuthDTO.setWebServerRedirectUri(oauthClientDetails.getWebServerRedirectUri());
        clientAuthDTO.setAuthorities(oauthClientDetails.getAuthorities());
        clientAuthDTO.setAccessTokenValidity(oauthClientDetails.getAccessTokenValidity());
        clientAuthDTO.setRefreshTokenValidity(oauthClientDetails.getRefreshTokenValidity());
        clientAuthDTO.setAdditionalInformation(oauthClientDetails.getAdditionalInformation());
        clientAuthDTO.setAutoapprove(oauthClientDetails.getAutoapprove());
        return clientAuthDTO;
    }
}
