package com.social_media.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {
  private static final String TRANSACTION_KEY = "transactionId";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest httpServletRequest,
      @NonNull HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String transactionId = UUID.randomUUID().toString();

      MDC.put(TRANSACTION_KEY, transactionId);

      filterChain.doFilter(httpServletRequest, httpServletResponse);
    } finally {
      MDC.clear();
    }
  }
}
