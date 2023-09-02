package scs.planus.global.auth.service.kakao;

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
public class KakaoOAuthUserProvider {

    private static final String KAKAO_PROVIDER_NAME = "kakao";

    private final KakaoAccessTokenClient kakaoAccessTokenClient;
    private final KakaoUserInfoClient kakaoUserInfoClient;

    public MemberProfile getUserInfo(String code) {
        OAuth2TokenResponseDto token = kakaoAccessTokenClient.getToken(code);
        Map<String, Object> userAttributes = kakaoUserInfoClient.getUserAttributes(token.getAccessToken());
        MemberProfile profile = OAuthAttributes.extract(KAKAO_PROVIDER_NAME, userAttributes);
        return profile;
    }
}
