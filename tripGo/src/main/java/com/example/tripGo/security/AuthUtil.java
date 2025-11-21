package com.example.tripGo.security;

import com.example.tripGo.entity.User;
import com.example.tripGo.entity.type.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecret;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public AuthProviderType getProviderType(String id) {
        return switch (id.toLowerCase()) {
            case "google" -> AuthProviderType.GOOGLE;
            case "github" -> AuthProviderType.GITHUB;
            default -> throw new IllegalArgumentException("Unsupported provider: " + id);
        };
    }

    public String determineProviderId(OAuth2User oAuth2User, String registrationId) {
        String providerId = switch (registrationId.toLowerCase()) {
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("id").toString();

            default -> {
                log.error("Unsupported OAuth2 provider: {}", registrationId);
                throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
            }
        };

        if (providerId == null || providerId.isBlank()) {
            log.error("Unable to determine providerId for provider: {}", registrationId);
            throw new IllegalArgumentException("Unable to determine providerId for OAuth2 login");
        }
        return providerId;
    }

    public String determineUsername(OAuth2User user, String id, String providerId) {
        String email = user.getAttribute("email");
        if (email != null && !email.isBlank()) return email;
        return switch (id.toLowerCase()) {
            case "google" -> user.getAttribute("sub");
            case "github" -> user.getAttribute("login");
            default -> providerId;
        };
    }
}
