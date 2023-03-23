package scs.planus.auth.userinfo.attribute;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.Member;
import scs.planus.domain.Role;
import scs.planus.domain.SocialType;
import scs.planus.domain.Status;

@Getter
@Builder
public class MemberProfile {

    private String nickname;
    private String email;
    private SocialType socialType;

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .socialType(socialType)
                .status(Status.ACTIVE)
                .role(Role.USER)
                .build();
    }
}
