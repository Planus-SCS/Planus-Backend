package scs.planus.auth.userinfo.impl;

import lombok.AllArgsConstructor;
import scs.planus.auth.userinfo.OAuth2UserInfo;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getRegistrationId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) getKakaoAccount().get("email");
    }

    @Override
    public String getNickName() {
        return (String) getProfile().get("nickname");
    }

    public String getDefaultProfileImage() {
        return (String) attributes.get("default_profile_image");
    }

    public Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    public Map<String, Object> getProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }
}
