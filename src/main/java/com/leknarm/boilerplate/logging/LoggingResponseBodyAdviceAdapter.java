package com.leknarm.boilerplate.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leknarm.boilerplate.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.StandardCharsets;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class LoggingResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (request instanceof ServletServerHttpRequest && response instanceof ServletServerHttpResponse) {
            if (!WebUtils.isActuatorEndpoints(((ServletServerHttpRequest) request).getServletRequest())) {
                logResponse(((ServletServerHttpRequest) request).getServletRequest(), ((ServletServerHttpResponse) response).getServletResponse(), body);
            }
        }
        return body;
    }

    public void logResponse(HttpServletRequest request, HttpServletResponse response, Object body) {
        log.info("Outgoing response traceparent: {}", request.getHeaders("traceparent"));
        StringBuilder builder = new StringBuilder("Outgoing response: ");
        builder.append(request.getProtocol()).append(" ").append(response.getStatus()).append(" ");
        if (body != null) {
            try {
                builder.append(objectMapper.writeValueAsString(body));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
        log.info(builder.toString(), StandardCharsets.UTF_8);
    }
}