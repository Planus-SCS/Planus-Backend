package scs.planus.global.auth.entity.userinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.Role;
import scs.planus.domain.member.entity.SocialType;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleUserInfo implements OAuthUserInfo {

    private String email;
    @JsonProperty("name")
    private String nickname;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
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

}
