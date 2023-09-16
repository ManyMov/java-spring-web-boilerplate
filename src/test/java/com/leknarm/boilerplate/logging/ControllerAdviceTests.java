package com.leknarm.boilerplate.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leknarm.boilerplate.logging.LoggingRequestBodyAdviceAdapter;
import com.leknarm.boilerplate.logging.LoggingResponseBodyAdviceAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerAdviceTests {
    private LoggingRequestBodyAdviceAdapter loggingRequestBodyAdviceAdapter;
    private LoggingResponseBodyAdviceAdapter loggingResponseBodyAdviceAdapter;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setUp() {
        httpServletRequest = mock(HttpServletRequest.class);
        ObjectMapper objectMapper = new ObjectMapper();
        loggingRequestBodyAdviceAdapter = new LoggingRequestBodyAdviceAdapter(httpServletRequest, objectMapper);
        loggingResponseBodyAdviceAdapter = new LoggingResponseBodyAdviceAdapter(objectMapper);
    }

    @Test
    public void testCheckSupportShouldBeTrue() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        assertTrue(loggingRequestBodyAdviceAdapter.supports(methodParameter,targetType, StringHttpMessageConverter.class));
        assertTrue(loggingResponseBodyAdviceAdapter.supports(methodParameter, StringHttpMessageConverter.class));
    }

    @Test
    public void testAfterBodyReadWithActuatorShouldBeSuccess() {
        String requestBody = """
                {"data": "test"}""";
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/actuator"));

        loggingRequestBodyAdviceAdapter.afterBodyRead(requestBody, httpInputMessage, methodParameter, targetType, StringHttpMessageConverter.class);
    }

    @Test
    public void testAfterBodyReadWithNormalBodyShouldBeSuccess() {
        String requestBody = """
                {"data": "test"}""";
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/hello"));
        when(httpServletRequest.getParameterNames()).thenReturn(Collections.enumeration(List.of("query")));
        when(httpServletRequest.getParameter("query")).thenReturn("data");
        when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Content-Type")));
        when(httpServletRequest.getHeader("Content-Type")).thenReturn("application/json");

        loggingRequestBodyAdviceAdapter.afterBodyRead(requestBody, httpInputMessage, methodParameter, targetType, StringHttpMessageConverter.class);
    }

    @Test
    public void testAfterBodyReadWithEmptyParameterShouldBeSuccess() {
        String requestBody = """
                {"data": "test"}""";
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/hello"));
        when(httpServletRequest.getParameterNames()).thenReturn(Collections.enumeration(List.of()));
        when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Content-Type")));
        when(httpServletRequest.getHeader("Content-Type")).thenReturn("application/json");

        loggingRequestBodyAdviceAdapter.afterBodyRead(requestBody, httpInputMessage, methodParameter, targetType, StringHttpMessageConverter.class);
    }

    @Test
    public void testAfterBodyReadWithWrongJsonShouldBeSuccessWithLogError() throws JsonProcessingException {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        loggingRequestBodyAdviceAdapter = new LoggingRequestBodyAdviceAdapter(httpServletRequest, objectMapper);
        when(objectMapper.writeValueAsString(any(String.class))).thenThrow(JsonProcessingException.class);

        String requestBody = """
                {"data": "test"}""";
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);

        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/hello"));
        when(httpServletRequest.getParameterNames()).thenReturn(Collections.enumeration(List.of()));
        when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Content-Type")));
        when(httpServletRequest.getHeader("Content-Type")).thenReturn("application/json");

        loggingRequestBodyAdviceAdapter.afterBodyRead(requestBody, httpInputMessage, methodParameter, targetType, StringHttpMessageConverter.class);
    }

    @Test
    public void testBeforeBodyWriteWithActuatorShouldBeSuccess() {
        String responseBody = """
                {"code": 200, "message": "success", "data": "test"}""";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ServletServerHttpRequest serverHttpRequest = mock(ServletServerHttpRequest.class);
        ServletServerHttpResponse serverHttpResponse = mock(ServletServerHttpResponse.class);
        when(serverHttpRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/actuator"));

        loggingResponseBodyAdviceAdapter.beforeBodyWrite(responseBody, methodParameter, MediaType.APPLICATION_JSON, StringHttpMessageConverter.class, serverHttpRequest, serverHttpResponse);
    }

    @Test
    public void testBeforeBodyWriteWithNormalShouldBeSuccess() {
        String responseBody = """
                {"code": 200, "message": "success", "data": "test"}""";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ServletServerHttpRequest serverHttpRequest = mock(ServletServerHttpRequest.class);
        ServletServerHttpResponse serverHttpResponse = mock(ServletServerHttpResponse.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        when(serverHttpRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(serverHttpResponse.getServletResponse()).thenReturn(httpServletResponse);
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/hello"));
        when(httpServletRequest.getProtocol()).thenReturn("HTTP/1.1");
        when(httpServletResponse.getStatus()).thenReturn(200);

        loggingResponseBodyAdviceAdapter.beforeBodyWrite(responseBody, methodParameter, MediaType.APPLICATION_JSON, StringHttpMessageConverter.class, serverHttpRequest, serverHttpResponse);
    }

    @Test
    public void testBeforeBodyWriteWithJsonProcessingExceptionShouldBeSuccessWithErrorLog() throws JsonProcessingException {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        loggingResponseBodyAdviceAdapter = new LoggingResponseBodyAdviceAdapter(objectMapper);

        String responseBody = """
                {"code": 200, "message": "success", "data": "test"}""";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ServletServerHttpRequest serverHttpRequest = mock(ServletServerHttpRequest.class);
        ServletServerHttpResponse serverHttpResponse = mock(ServletServerHttpResponse.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        when(serverHttpRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(serverHttpResponse.getServletResponse()).thenReturn(httpServletResponse);
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/hello"));
        when(httpServletRequest.getProtocol()).thenReturn("HTTP/1.1");
        when(httpServletResponse.getStatus()).thenReturn(200);
        when(objectMapper.writeValueAsString(responseBody)).thenThrow(JsonProcessingException.class);

        loggingResponseBodyAdviceAdapter.beforeBodyWrite(responseBody, methodParameter, MediaType.APPLICATION_JSON, StringHttpMessageConverter.class, serverHttpRequest, serverHttpResponse);
    }

}
