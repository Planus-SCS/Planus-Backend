package scs.planus.global.auth.dto;

import lombok.Builder;
import lombok.Getter;
import scs.planus.domain.member.entity.Member;
import scs.planus.global.auth.entity.Token;

@Getter
@Builder
public class OAuthLoginResponseDto {

    private Long memberId;
    private String accessToken;
    private String refreshToken;

    public static OAuthLoginResponseDto of(Member member, Token token) {
        return OAuthLoginResponseDto.builder()
                .memberId(member.getId())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }
}
