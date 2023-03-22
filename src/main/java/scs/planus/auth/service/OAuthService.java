package scs.planus.auth.service;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import scs.planus.auth.dto.OAuth2TokenResponseDto;
import scs.planus.auth.dto.OAuthLoginResponseDto;

import java.util.Map;

public interface OAuthService {
    OAuthLoginResponseDto login(String clientName, String code);

    OAuth2TokenResponseDto getToken(ClientRegistration client, String code);

    Map<String, Object> getUserAttributes(ClientRegistration client, OAuth2TokenResponseDto tokenResponse);

}
