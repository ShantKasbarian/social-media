package com.social_media.service.impl;

import com.social_media.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("generating token for user with username {}", username);

        Map<String, Object> claims = new HashMap<>();

        String token = Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
                .and()
                .signWith(getKey())
                .compact();

        log.info("generated token for user with username {}", username);

        return token;
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        String userDetailsUsername = userDetails.getUsername();

        log.info("validating token of userDetails username {}", userDetailsUsername);

        final String username = extractUsername(token);
        boolean isValid = username.equals(userDetailsUsername) && !isTokenExpired(token);

        log.info("validated token of userDetails username {}", userDetailsUsername);

        return isValid;
    }

    @Override
    public String extractUsername(String token) {
        log.info("extracting username from token");

        String username = extractClaim(token, Claims::getSubject);

        log.info("extracted username {} from token", username);

        return username;
    }

    @Override
    public String fetchToken(HttpServletRequest request) {
        String requestId = request.getRequestId();

        log.info("fetching token from request with id {}", requestId);

        String authHeader = request.getHeader(AUTHORIZATION);
        String token = null;

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            token = authHeader.substring(BEGIN_INDEX);
        }

        log.info("fetched token from request with id {}", requestId);

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
