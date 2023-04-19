package scs.planus.global.auth.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.entity.Role;
import scs.planus.domain.member.entity.SocialType;
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
