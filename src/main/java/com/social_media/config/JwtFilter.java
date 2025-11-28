package com.social_media.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    static final String AUTHORIZATION = "Authorization";

    static final String BEARER = "Bearer ";

    private static final int BEGIN_INDEX = 7;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal (
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION);
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            token = authHeader.substring(BEGIN_INDEX);

            try {
                username = jwtService.extractUsername(token);
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            createAuthenticationToken(request, username, token);
        }

        filterChain.doFilter(request, response);
    }

    private void createAuthenticationToken(HttpServletRequest request, String username, String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        try {
            if (jwtService.validateToken(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken (
                        userDetails, null, userDetails.getAuthorities()
                );
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                authToken.setDetails(webAuthenticationDetails);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
