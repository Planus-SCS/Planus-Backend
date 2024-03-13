package scs.planus.global.auth.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApplePublicKey {
    private String kty; // key 유형 매개변수 설정. "RSA"로 설정해야함.
    private String kid; // Apple developer account 에서 얻은 identity key
    private String use; // public key 의 용도
    private String alg; // 토큰을 암호화 하는데 사용된 암호화 알고리즘
    private String n; // RSA public key 의 모듈러스 값
    private String e; // RSA public key 의 지수 값
}


