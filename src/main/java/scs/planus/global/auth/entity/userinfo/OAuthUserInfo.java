package scs.planus.global.auth.entity.userinfo;

import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.SocialType;

public interface OAuthUserInfo {
    String getEmail();

    String getNickname();

    SocialType getSocialType();

    Member toMember();
}
