package scs.planus.global.auth.service.apple;

import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import scs.planus.global.auth.entity.apple.ApplePublicKey;
import scs.planus.global.auth.entity.apple.ApplePublicKeys;
import scs.planus.global.exception.PlanusException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import static scs.planus.global.exception.CustomExceptionStatus.APPLE_PUBLIC_KEY_GENERATION_ERROR;

@Component
public class ApplePublicKeyGenerator {
    private static final String SIGN_ALGORITHM_HEADER_KEY = "alg";
    private static final String KEY_ID_HEADER_KEY = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;

    public PublicKey generatePublicKey(Map<String, String> headers, ApplePublicKeys applePublicKeys) {
        ApplePublicKey applePublicKey =
                applePublicKeys.getMatchesKey(headers.get(SIGN_ALGORITHM_HEADER_KEY), headers.get(KEY_ID_HEADER_KEY));

        return generatePublicKeyWithApplePublicKey(applePublicKey);
    }

    private PublicKey generatePublicKeyWithApplePublicKey(ApplePublicKey publicKey) throws PlanusException {
        byte[] nBytes = Base64Utils.decodeFromUrlSafeString(publicKey.getN());
        byte[] eBytes = Base64Utils.decodeFromUrlSafeString(publicKey.getE());

        BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getKty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new PlanusException(APPLE_PUBLIC_KEY_GENERATION_ERROR);
        }
    }
}
