package scs.planus.auth.userinfo.attribute;

import scs.planus.auth.userinfo.impl.GoogleUserInfo;
import scs.planus.auth.userinfo.impl.KakaoUserInfo;
import scs.planus.common.exception.PlanusException;
import scs.planus.domain.SocialType;

import java.util.Arrays;
import java.util.Map;

import static scs.planus.common.response.CustomResponseStatus.NONE_SOCIAL_TYPE;

public enum OAuthAttributes {

    KAKAO("kakao") {
        public MemberProfile of(Map<String, Object> attributes) {
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            return MemberProfile.builder()
                    .nickname(kakaoUserInfo.getNickName())
                    .email(kakaoUserInfo.getEmail())
                    .socialType(SocialType.KAKAO)
                    .build();
        }
    },

    GOOGLE("google") {
        public MemberProfile of(Map<String, Object> attributes) {
            GoogleUserInfo googleUserInfo = new GoogleUserInfo(attributes);
            return MemberProfile.builder()
                    .nickname(googleUserInfo.getNickName())
                    .email(googleUserInfo.getEmail())
                    .socialType(SocialType.GOOGLE)
                    .build();
        }
    };

    private final String socialType;

    OAuthAttributes(String socialType) {
        this.socialType = socialType;
    }

    public static MemberProfile extract(String providerName, Map<String, Object> userAttributes) {
        return Arrays.stream(values())
                .filter(provider -> providerName.equals(provider.socialType))
                .findFirst()
                .orElseThrow(() -> new PlanusException(NONE_SOCIAL_TYPE))
                .of(userAttributes);
    }

    public abstract MemberProfile of(Map<String, Object> attributes);
}

