package com.hussein221.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Jwt {
    @Getter
    private final String token;
    @Getter
    private final Long userId;
    @Getter
    private final LocalDateTime issuedAt;
    @Getter
    private final LocalDateTime expiration;

    public Jwt(String token, Long userId, LocalDateTime issuedAt, LocalDateTime expiration) {
        this.token = token;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }
    public static Jwt of(Long userId, Long validityInMinutes, String secretKey) {
            var issuedAt = Instant.now();
            var expiration = issuedAt.plus(validityInMinutes, ChronoUnit.MINUTES);
            return new Jwt(
                    Jwts.builder()
                        .claim("user_id",userId)
                        .setIssuedAt(Date.from(issuedAt))
                        .setExpiration(Date.from(expiration))
                        .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secretKey.getBytes(UTF_8)))
                        .compact(),
                    userId,
                    LocalDateTime.ofInstant(issuedAt, ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(expiration,ZoneId.systemDefault()));
    }

    public static Jwt from (String token,String secretKey) {
        var claims = (Claims) Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes(UTF_8)))
                .build()
                .parse(token)
                .getBody();
        var userId = claims.get("user_id",Long.class);
        var issuedAt = claims.getIssuedAt();
        var expiration = claims.getExpiration();

        return new Jwt(
                token,
                userId,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(issuedAt.getTime()),ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration.getTime()),ZoneId.systemDefault())
                );
    }
}
