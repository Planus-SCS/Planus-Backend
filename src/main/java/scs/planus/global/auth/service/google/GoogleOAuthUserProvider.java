package scs.planus.global.auth.service.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import scs.planus.global.auth.dto.OAuthTokenResponseDto;
import scs.planus.global.auth.entity.OAuthUserInfo;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthUserProvider {

    private static final String GOOGLE_PROVIDER_NAME = "google";

    private final GoogleUserInfoClient googleUserInfoClient;
    private final GoogleAccessTokenClient googleAccessTokenClient;

    public OAuthUserInfo getUserInfo(String code) {
        OAuthTokenResponseDto token = googleAccessTokenClient.getToken(code);
        OAuthUserInfo googleUserInfo = googleUserInfoClient.getUserAttributes(token.getAccessToken());
        return googleUserInfo;
    }
}
