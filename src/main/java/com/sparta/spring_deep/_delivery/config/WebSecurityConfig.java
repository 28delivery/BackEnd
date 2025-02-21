package com.sparta.spring_deep._delivery.config;

import com.sparta.spring_deep._delivery.domain.user.jwt.JwtAuthorizationFilter;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsServiceImpl;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    /**
     * 비밀번호 암호화
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Spring Security Filtering
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(csrf -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        );

        // URL 접근 범위
        http.authorizeHttpRequests(authorizeHttpRequests ->
            authorizeHttpRequests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll() // resources 접근 허용 설정
                .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                // 사용자 - 회원 가입/로그인 : 요청 모두 접근 허가
                .requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                // 사용자 - 내 정보 조회/로그 아웃/사용자 수정 및 삭제/비번 변경 : 요청 인증처리
                .requestMatchers("/api/users/me", "/api/users/logout", "/api/users/**",
                    "/api/users/{username}/password").authenticated()
                .anyRequest().authenticated() // 그 외 모든 요청 인증처리)
        );

        //  로그인 폼 페이지 설정
        //http.formLogin((formLogin) ->
        //       formLogin
        //                .loginPage("/api/users/login-page").permitAll()
        //                .loginProcessingUrl("/api/users/login")
        //);

        // JWT 인증 & 인가 필터 설정
        // jwtAuthenticationFilter가 UserService login과정에서 인증처리 된거면
        // jwtAuthenticationFilter 없어도 되나?
        // => jwtAuthenticationFilter의 attemp메소드랑 UserService login이랑 기능이 같음
        // => 그래서 jwtAuthenticationFilter 미사용
        // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JWT 필터
     *
     * @return
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }
}
