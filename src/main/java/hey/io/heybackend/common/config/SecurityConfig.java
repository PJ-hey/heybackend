package hey.io.heybackend.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.log.MDCRequestLoggingFilter;
import hey.io.heybackend.common.security.jwt.JwtAuthenticationEntryPoint;
import hey.io.heybackend.common.security.jwt.JwtAuthenticationFilter;
import hey.io.heybackend.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final String[] permitAllEndpointList = {
            "/health_check",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/login/oauth2/code/google",
            "/oauth2/login",
            "/oauth2/google/login",
            "/oauth2/kakao/login",
            "/oauth2/apple/login",
            "/oauth2/refresh",
            "/oauth2/expiredJwt",
            "/performances",
            "/performances/search",
            "/performances/new",
            "/performances/rank",
            "/performances/notification",
            "/artists/search",
            "/artists/rank",
            "/message/**",
            "/h2-console/**"
    };

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sesseion -> sesseion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, permitAllEndpointList),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationEntryPoint(objectMapper()),
                        JwtAuthenticationFilter.class)
                .addFilterBefore(new MDCRequestLoggingFilter(), JwtAuthenticationEntryPoint.class);

        return http.build();
    }
}
