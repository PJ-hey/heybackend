package hey.io.heybackend.domain.oauth.controller;


import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.common.response.SuccessResponse;
import hey.io.heybackend.common.security.jwt.JwtTokenInfo;
import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import hey.io.heybackend.common.security.jwt.JwtType;
import hey.io.heybackend.common.utils.HeaderUtils;
import hey.io.heybackend.domain.oauth.dto.RequestAppleLogin;
import hey.io.heybackend.domain.oauth.dto.ResponseAccessToken;
import hey.io.heybackend.domain.oauth.dto.ResponseJwtToken;
import hey.io.heybackend.domain.oauth.service.AppleOAuthService;
import hey.io.heybackend.domain.oauth.service.GoogleOAuthService;
import hey.io.heybackend.domain.oauth.service.KakaoOAuthService;
import hey.io.heybackend.domain.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtTokenProvider jwtTokenProvider;


    @Operation(summary = "Google Login", description = "Google Login API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AccessToken, RefreshToken 반환"),
            @ApiResponse(
                    responseCode = "500",
                    description = "R001 : 외부로의 REST 통신에 실패하였습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(
                    responseCode = "404",
                    description = "S007: authorization header가 비었습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/google/login")
    public ResponseEntity<SuccessResponse<ResponseJwtToken>> googleLogin(HttpServletRequest request) {
        return SuccessResponse.of(googleOAuthService.login(request)).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "Apple Login", description = "Google Login API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AccessToken, RefreshToken 반환"),
            @ApiResponse(
                    responseCode = "500",
                    description = "R001 : 외부로의 REST 통신에 실패하였습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(
                    responseCode = "404",
                    description = "S007: authorization header가 비었습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/apple/login")
    public ResponseEntity<SuccessResponse<ResponseJwtToken>> appleLogin(HttpServletRequest request,
                                                                        @RequestBody @Valid RequestAppleLogin requestAppleLogin) {
        return SuccessResponse.of(appleOAuthService.login(request, requestAppleLogin)).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "Kakao Login", description = "Google Login API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AccessToken, RefreshToken 반환"),
            @ApiResponse(
                    responseCode = "500",
                    description = "R001 : 외부로의 REST 통신에 실패하였습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(
                    responseCode = "404",
                    description = "S007: authorization header가 비었습니다.",
                    content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/kakao/login")
    public ResponseEntity<SuccessResponse<ResponseJwtToken>> kakaoLogin(HttpServletRequest request) {
        return SuccessResponse.of(kakaoOAuthService.login(request)).asHttp(HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<SuccessResponse<ResponseAccessToken>> getAccessToken(HttpServletRequest request) {
        String refreshToken = HeaderUtils.getJwtToken(request, JwtType.REFRESH);
        String accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);
        return SuccessResponse.of(ResponseAccessToken.of(accessToken)).asHttp(HttpStatus.OK);
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteAccount(@AuthUser JwtTokenInfo tokenInfo) {
        oAuthService.deleteAccount(tokenInfo.getUserId());
        return ResponseEntity.noContent().build();
    }

}
