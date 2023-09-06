package scs.planus.global.auth.entity.userinfo;

import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.Role;
import scs.planus.domain.member.entity.SocialType;

public class AppleUserInfo implements OAuthUserInfo {

    private String nickname;
    private String email;

    public AppleUserInfo(String email) {
        this.email = email;
    }

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
        return SocialType.APPLE;
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

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
