package com.sparta.spring_deep._delivery.config;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsServiceImpl;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtAuthenticationFilter;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtAuthorizationFilter;
import com.sparta.spring_deep._delivery.domain.user.jwt.JwtUtil;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final UserRepository userRepository;
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
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                // 사용자 - 회원 가입/로그인 : 요청 모두 접근 허가
                .requestMatchers(
                    "/api/users/signup",
                    "/api/users/login",
                    "/api/restaurants/search",
                    "/api/reviews/{restaurantId}/search?"
                ).permitAll()

                .requestMatchers(
                    "/admin/**"
                ).hasAuthority("ADMIN")

                // restaurant api
                .requestMatchers(HttpMethod.POST,
                    "/api/restaurants"
                ).hasAnyAuthority("OWNER", "ADMIN")

                .requestMatchers(HttpMethod.GET,
                    "/api/restaurants/{restaurantId}"
                ).permitAll()

                .requestMatchers(HttpMethod.PUT,
                    "/api/restaurants/{restaurantsId}"
                ).hasAnyAuthority("OWNER", "ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/restaurants/{restaurantId}"
                ).hasAnyAuthority("OWNER", "ADMIN")

                // Menu
                .requestMatchers(HttpMethod.POST,
                    "/api/menus/{restaurantId}",
                    "/api/menus/{menuId}/aiDescription"
                ).hasAnyAuthority("OWNER", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.GET,
                    "/api/menus/{restaurantId}"
                ).permitAll()

                .requestMatchers(HttpMethod.PUT,
                    "/api/menus/{menuId}"
                ).hasAnyAuthority("OWNER", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/menus/{menuId}"
                ).hasAnyAuthority("OWNER", "MANAGER", "ADMIN")

                // Order
                .requestMatchers(HttpMethod.POST,
                    "/api/orders/"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.PUT,
                    "/api/orders/{orderId}/status"
                ).hasAnyAuthority("OWNER", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.GET,
                    "/api/orders/me",
                    "/api/orders/polling"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                // Review
                .requestMatchers(HttpMethod.POST,
                    "/api/reviews"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.PUT,
                    "/api/reviews/{reviewId}"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/reviews/{reviewId}"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                // Address
                .requestMatchers(HttpMethod.POST,
                    "/api/addresses"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.GET,
                    "/api/addresses"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.PUT,
                    "/api/addresses/{addressId}"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/addresses/{addressId}"
                ).hasAnyAuthority("CUSTOMER", "ADMIN")

                // Restaurant Address
                .requestMatchers(HttpMethod.POST,
                    "/api/restaurantAddresses"
                ).hasAnyAuthority("OWNER", "ADMIN")

                .requestMatchers(HttpMethod.GET,
                    "/api/restaurantAddresses"
                ).hasAnyAuthority("OWNER", "ADMIN")

                .requestMatchers(HttpMethod.PUT,
                    "/api/restaurantAddresses/{addressId}"
                ).hasAnyAuthority("OWNER", "ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/restaurantAddresses/{addressId}"
                ).hasAnyAuthority("OWNER", "ADMIN")

                .anyRequest().authenticated() // 그 외 모든 요청 인증처리)
        );

        // JWT 인증 & 인가 필터 설정
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JPA 인증 필터
     *
     * @return
     * @throws Exception
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        return filter;
    }

    /**
     * JWT 인가 필터
     *
     * @return
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }
}
