package scs.planus.global.auth.entity.userinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.Role;
import scs.planus.domain.member.entity.SocialType;

@AllArgsConstructor
public class KakaoUserInfo implements OAuthUserInfo {

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getNickname() {
        return kakaoAccount.getNickname();
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }

    @Override
    public Member toMember() {
        return Member.builder()
                .nickname(getNickname())
                .email(getEmail())
                .socialType(getSocialType())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {

        private String email;
        private Profile profile;

        public String getNickname() {
            return profile.getNickname();
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {
            String nickname;
        }
    }
}
