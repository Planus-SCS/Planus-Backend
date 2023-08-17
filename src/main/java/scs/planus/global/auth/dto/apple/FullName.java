package scs.planus.global.auth.dto.apple;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FullName {
    private String givenName;
    private String familyName;
}
