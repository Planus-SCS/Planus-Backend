package scs.planus.global.auth.entity.apple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.global.exception.PlanusException;

import java.util.List;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_ALG_KID_INFO;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApplePublicKeys {
    private List<ApplePublicKey> keys;

    public ApplePublicKey getMatchesKey(String alg, String kid) {
        return this.keys
                .stream()
                .filter(k -> k.getAlg().equals(alg) && k.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new PlanusException(INVALID_ALG_KID_INFO));
    }
}
