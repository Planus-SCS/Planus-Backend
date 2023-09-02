package scs.planus.global.auth.service.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import scs.planus.global.auth.dto.OAuth2TokenResponseDto;
import scs.planus.global.auth.entity.MemberProfile;
import scs.planus.global.auth.entity.OAuthAttributes;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthUserProvider {

    private static final String GOOGLE_PROVIDER_NAME = "google";

    private final GoogleUserInfoClient googleUserInfoClient;
    private final GoogleAccessTokenClient googleAccessTokenClient;

    public MemberProfile getUserInfo(String code) {
        OAuth2TokenResponseDto token = googleAccessTokenClient.getToken(code);
        Map<String, Object> userAttributes = googleUserInfoClient.getUserAttributes(token.getAccessToken());
        MemberProfile profile = OAuthAttributes.extract(GOOGLE_PROVIDER_NAME, userAttributes);
        return profile;
    }
}
