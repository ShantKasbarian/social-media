package com.social_media.service.impl;

import com.social_media.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private static final int BEGIN_INDEX = 7;

    private static final String KEY_ALGORITHM_NAME = "HmacSHA256";

    private String key = "";

    public JwtServiceImpl() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_NAME);
        SecretKey secretKey = keyGenerator.generateKey();
        key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    @Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String name = extractUsername(token);
        return (name.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String fetchToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        String token = null;

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            token = authHeader.substring(BEGIN_INDEX);
        }

        return token;
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
