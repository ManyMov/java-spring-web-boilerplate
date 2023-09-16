package com.leknarm.boilerplate.filter;

import com.leknarm.boilerplate.filter.CorrelationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CorrelationFilterTests {

    private static final String X_CORRELATION_ID = "x-correlation-id";
    private static final String CORRELATION_ID = "CORRELATION_ID";

    @Spy
    public CorrelationFilter correlationFilter;

    @Test
    public void testFilterWithNullCorrelationShouldBeSuccess() throws ServletException, IOException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(httpServletRequest.getHeader(X_CORRELATION_ID)).thenReturn(null);
        correlationFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Test
    public void testFilterWithEmptyCorrelationShouldBeSuccess() throws ServletException, IOException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(httpServletRequest.getHeader(X_CORRELATION_ID)).thenReturn("");
        correlationFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Test
    public void testFilterWithSpecificCorrelationShouldBeSuccess() throws ServletException, IOException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(httpServletRequest.getHeader(X_CORRELATION_ID)).thenReturn("abcde-fghij-klmno-pqrst-uvwxy");
        correlationFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

}
