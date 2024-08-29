package hey.io.heybackend.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import hey.io.heybackend.common.utils.HeaderUtils;
import hey.io.heybackend.domain.oauth.dto.OAuthAccessToken;
import hey.io.heybackend.domain.oauth.dto.OAuthUserProfile;
import hey.io.heybackend.domain.oauth.dto.ResponseJwtToken;
import hey.io.heybackend.domain.oauth.properties.KakaoProperties;
import hey.io.heybackend.domain.oauth.repository.AuthRepository;
import hey.io.heybackend.domain.user.domain.SocialCode;
import hey.io.heybackend.domain.user.domain.User;
import hey.io.heybackend.domain.user.service.UserService;
import hey.io.heybackend.domain.user.service.ValidateUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static hey.io.heybackend.common.exception.ErrorCode.PARSING_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final KakaoProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final ValidateUserService validateUserService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseJwtToken login(HttpServletRequest request) {
        OAuthAccessToken accessToken = getAccessToken(request);

        OAuthUserProfile oAuthUserProfile = getUserProfile(accessToken);

        User user = validateUserService.validateRegisteredUserByEmail(oAuthUserProfile.getEmail(), SocialCode.KAKAO);

        if (user == null) {
            user = userService.registerKakaoUser(oAuthUserProfile.getEmail(), SocialCode.KAKAO, accessToken.getRefreshToken());
        }

        String jwtAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getUserRole());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(), user.getUserRole());

        return ResponseJwtToken.of(jwtAccessToken, jwtRefreshToken);
    }

    private OAuthAccessToken getAccessToken(HttpServletRequest request) {
        String authorizationCode = HeaderUtils.getAuthCode(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
        httpBody.add("code", authorizationCode);
        httpBody.add("client_id", properties.getClientId());
        httpBody.add("client_secret", properties.getClientSecret());
        httpBody.add("redirect_uri", properties.getRedirectUri());
        httpBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestToken = new HttpEntity<>(httpBody,
                httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
                properties.getTokenUrl(), requestToken, String.class);

        try {
            return objectMapper.readValue(response.getBody(), OAuthAccessToken.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR, e);
        }
    }

    private OAuthUserProfile getUserProfile(OAuthAccessToken accessToken) {

        String userInfoUrl = properties.getUserInfoUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getAccessToken());
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
                httpEntity, String.class);

        try {
            return objectMapper.readValue(userInfoResponse.getBody(), OAuthUserProfile.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR, e);
        }
    }

}
