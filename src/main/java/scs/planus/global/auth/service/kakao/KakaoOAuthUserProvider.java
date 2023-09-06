package scs.planus.global.auth.service.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import scs.planus.global.auth.dto.OAuthTokenResponseDto;
import scs.planus.global.auth.entity.KakaoUserInfo;
import scs.planus.global.auth.entity.OAuthUserInfo;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthUserProvider {

    private static final String KAKAO_PROVIDER_NAME = "kakao";

    private final KakaoAccessTokenClient kakaoAccessTokenClient;
    private final KakaoUserInfoClient kakaoUserInfoClient;

    public OAuthUserInfo getUserInfo(String code) {
        OAuthTokenResponseDto token = kakaoAccessTokenClient.getToken(code);
        KakaoUserInfo kakaoUserInfo = kakaoUserInfoClient.getUserAttributes(token.getAccessToken());
        return kakaoUserInfo;
    }
}
