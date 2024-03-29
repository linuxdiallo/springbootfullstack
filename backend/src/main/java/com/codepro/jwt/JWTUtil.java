package com.codepro.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author aboubacar.diallo
 * @created 04/08/2023 - 15:42
 * @project springbootfullstack
 * @package com.codepro.jwt
 */

@Service
public class JWTUtil {

    @Value("${jwt.token}")
    private String secretKey;

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String ...scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }
    public String issueToken(String subject, Map<String, Object> claims) {

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://realize-soft.com")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(
                        Date.from(
                                Instant.now().plus(1, ChronoUnit.DAYS)
                        )
                ).signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;

    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isTokenValid(String jwt,
                                String username) {
        return getSubject(jwt).equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}
