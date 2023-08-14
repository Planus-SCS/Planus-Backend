package scs.planus.global.util.resourceLoader;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import scs.planus.global.exception.PlanusException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;

import static scs.planus.global.exception.CustomExceptionStatus.INTERNAL_SERVER_ERROR;

public class ResourcesLoader {
    public static PrivateKey createPrivateKey(String keyPath){
        ClassPathResource resource = new ClassPathResource(keyPath);
        try {
            String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            Reader pemReader = new StringReader(privateKey);
            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            return converter.getPrivateKey(object);
        } catch (IOException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
    }
}
