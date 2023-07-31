package scs.planus.global.auth.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import scs.planus.domain.member.entity.SocialType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthAttributesTest {
    private static final String PLANUS_TEST_EMAIL = "planus@planus";
    private static final String PLANUS_TEST_NICKNAME = "planus";

    @Nested
    @DisplayName("KAKAO OAuthAttributesTest")
    class KAKAO_test {
        Map<String, Object> userAttributes;

        @BeforeEach
        void init() {
            userAttributes = Map.of(
                    "id", 1L,
                    "kakao_account", Map.of("email", PLANUS_TEST_EMAIL,
                            "profile", Map.of("nickname", PLANUS_TEST_NICKNAME)
                    )
            );
        }

        @DisplayName("KAKAO 의 of 메소드로 MemberProfile 를 생성할 수 있다.")
        @Test
        void of_kakao() {
            // when
            MemberProfile memberProfile = OAuthAttributes.KAKAO.of(userAttributes);

            // then
            assertThat(memberProfile.getEmail()).isEqualTo(PLANUS_TEST_EMAIL);
            assertThat(memberProfile.getNickname()).isEqualTo(PLANUS_TEST_NICKNAME);
            assertThat(memberProfile.getSocialType()).isEqualTo(SocialType.KAKAO);
        }

        @DisplayName("providerName 과 일치하는 SocialType의 MemberProfile 를 추출할 수 있다.")
        @Test
        void extract_kakao() {
            // given
            String providerName = "kakao";

            // when
            MemberProfile memberProfile = OAuthAttributes.extract(providerName, userAttributes);

            // then
            assertThat(memberProfile.getEmail()).isEqualTo(PLANUS_TEST_EMAIL);
            assertThat(memberProfile.getNickname()).isEqualTo(PLANUS_TEST_NICKNAME);
            assertThat(memberProfile.getSocialType()).isEqualTo(SocialType.KAKAO);
        }
    }

    @Nested
    @DisplayName("GOOGLE OAuthAttributesTest")
    class GOOGLE_test {
        Map<String, Object> userAttributes;

        @BeforeEach
        void init() {
            userAttributes = Map.of(
                    "sub", 1L,
                    "email", PLANUS_TEST_EMAIL,
                    "name", PLANUS_TEST_NICKNAME
            );
        }

        @DisplayName("providerName 과 일치하는 SocialType의 MemberProfile 를 추출할 수 있다.")
        @Test
        void extract_google() {
            // given
            String providerName = "google";

            // when
            MemberProfile memberProfile = OAuthAttributes.extract(providerName, userAttributes);

            // then
            assertThat(memberProfile.getEmail()).isEqualTo(PLANUS_TEST_EMAIL);
            assertThat(memberProfile.getNickname()).isEqualTo(PLANUS_TEST_NICKNAME);
            assertThat(memberProfile.getSocialType()).isEqualTo(SocialType.GOOGLE);
        }

        @DisplayName("GOOGLE 의 of 메소드로 MemberProfile 를 생성할 수 있다.")
        @Test
        void of_google() {
            // when
            MemberProfile memberProfile = OAuthAttributes.GOOGLE.of(userAttributes);

            // then
            assertThat(memberProfile.getEmail()).isEqualTo(PLANUS_TEST_EMAIL);
            assertThat(memberProfile.getNickname()).isEqualTo(PLANUS_TEST_NICKNAME);
            assertThat(memberProfile.getSocialType()).isEqualTo(SocialType.GOOGLE);
        }

    }
}