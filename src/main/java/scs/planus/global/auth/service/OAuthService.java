package scs.planus.global.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import scs.planus.domain.Status;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.auth.dto.OAuth2TokenResponseDto;
import scs.planus.global.auth.dto.OAuthLoginResponseDto;
import scs.planus.global.auth.entity.MemberProfile;
import scs.planus.global.auth.entity.OAuthAttributes;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.redis.RedisService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static scs.planus.global.exception.CustomExceptionStatus.ALREADY_EXIST_SOCIAL_ACCOUNT;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final InMemoryClientRegistrationRepository clientRegistrations;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Transactional
    public OAuthLoginResponseDto login(String providerName, String code) {
        ClientRegistration client = clientRegistrations.findByRegistrationId(providerName);
        Map<String, Object> attributes = getUserAttributes(client, code);
        MemberProfile profile = OAuthAttributes.extract(providerName, attributes);

        Member member = saveOrGetMember(profile);
        Token token = jwtProvider.generateToken(member.getEmail());
        redisService.saveValue(member.getEmail(), token);

        return OAuthLoginResponseDto.builder()
                .memberId(member.getId())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    private Map<String, Object> getUserAttributes(ClientRegistration client, String code) {
        OAuth2TokenResponseDto token = getToken(client, code);
        return WebClient.create()
                .post()
                .uri(client.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> {
                    header.setBearerAuth(token.getAccessToken());
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    private OAuth2TokenResponseDto getToken(ClientRegistration client, String code) {
        log.info("client=[{}], client.TokenUri=[{}]", client.getRegistrationId(), client.getProviderDetails().getTokenUri());
        return WebClient.create()
                .post()
                .uri(client.getProviderDetails().getTokenUri())
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(accessTokenRequest(client, code))
                .retrieve()
                .bodyToMono(OAuth2TokenResponseDto.class)
                .block();
    }

    private MultiValueMap<String, String> accessTokenRequest(ClientRegistration client, String code) {
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("code", code);
        data.add("client_id", client.getClientId());
        data.add("client_secret", client.getClientSecret());
        data.add("redirect_uri", client.getRedirectUri());
        data.add("grant_type", "authorization_code");
        return data;
    }

    private Member saveOrGetMember(MemberProfile profile) {
        Member member = memberRepository.findByEmail(profile.getEmail()).orElse(null);
        if (member == null) {
            member = memberRepository.save(profile.toEntity());
            return member;
        }

        if (!member.getSocialType().equals(profile.getSocialType())) {
            throw new PlanusException(ALREADY_EXIST_SOCIAL_ACCOUNT);
        }

        if (member.getStatus().equals(Status.INACTIVE)) {
            member.init(profile.getNickname());
        }
        return member;
    }
}
