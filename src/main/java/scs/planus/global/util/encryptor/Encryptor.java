package scs.planus.global.util.encryptor;

import scs.planus.global.exception.PlanusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static scs.planus.global.exception.CustomExceptionStatus.INTERNAL_SERVER_ERROR;

public class Encryptor {
    public static String encryptWithSHA256(String value) throws PlanusException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
    }

}
