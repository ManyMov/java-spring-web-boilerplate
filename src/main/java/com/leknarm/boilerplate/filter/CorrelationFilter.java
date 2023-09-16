package com.leknarm.boilerplate.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter implements Filter {

    private static final String X_CORRELATION_ID = "x-correlation-id";
    private static final String CORRELATION_ID = "CORRELATION_ID";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String xCorrelationId = request.getHeader(X_CORRELATION_ID);
        xCorrelationId = xCorrelationId == null || xCorrelationId.isEmpty() ? UUID.randomUUID().toString() : xCorrelationId;
        ThreadContext.put(CORRELATION_ID, xCorrelationId);
        filterChain.doFilter(request, response);
    }
}
