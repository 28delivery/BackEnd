package com.sparta.spring_deep._delivery.domain.user.jwt;

import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTime;
    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Create JWT
     *
     * @param username
     * @param role
     * @return
     */
    public String createJwt(String username, UserRole role) {
        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + expirationTime);

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .setExpiration(expirationDate) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    /**
     * get JWT from header
     *
     * @param request
     * @return
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Valicate JWT
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        if (isTokenBlacklisted(token)) {
            log.error("JWT token is blacklisted");
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty");
        }
        return false;
    }

    /**
     * get User Info from JWT
     *
     * @param token
     * @return
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void blacklist(String token) {
        jwtBlacklistRepository.addBlackList(token);
        log.info("Blacklisted token: " + token);
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtBlacklistRepository.isBlacklisted(token);
    }
}
