package com.leknarm.boilerplate.logging;

import com.leknarm.boilerplate.logging.LoggingClientHttpRequestInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoggingTests {

    @Spy
    public LoggingClientHttpRequestInterceptor loggingClientHttpRequestInterceptor;

    @Test
    public void testLoggingWithRequestBodyAndResponseBodyShouldBeSuccess() throws IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("/"));
        when(request.getHeaders()).thenReturn(requestHeaders);

        byte[] requestBody = """
                {"data": "test"}""".getBytes();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(2147L);
        responseHeaders.setContentType((MediaType.APPLICATION_JSON));

        byte[] responseBody = """
                {"code": 200, "message": "success", "data": "test"}""".getBytes();


        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(responseBody));

        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        when(clientHttpRequestExecution.execute(request, requestBody)).thenReturn(response);

        assertDoesNotThrow(() -> loggingClientHttpRequestInterceptor.intercept(request, requestBody, clientHttpRequestExecution));
    }

    @Test
    public void testLoggingWithRequestBodyAndResponseBodyTextShouldBeSuccess() throws IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("/"));
        when(request.getHeaders()).thenReturn(requestHeaders);

        byte[] requestBody = """
                {"data": "test"}""".getBytes();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(2147L);
        responseHeaders.setContentType((MediaType.TEXT_PLAIN));

        byte[] responseBody = "ok".getBytes();


        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(response.getHeaders()).thenReturn(responseHeaders);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(responseBody));

        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        when(clientHttpRequestExecution.execute(request, requestBody)).thenReturn(response);

        assertDoesNotThrow(() -> loggingClientHttpRequestInterceptor.intercept(request, requestBody, clientHttpRequestExecution));
    }

    @Test
    public void testLoggingWithRequestBodyAndWithoutResponseContentTypeShouldBeSuccess() throws IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("/"));
        when(request.getHeaders()).thenReturn(requestHeaders);

        byte[] requestBody = """
                {"data": "test"}""".getBytes();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(2147L);

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(response.getHeaders()).thenReturn(responseHeaders);

        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        when(clientHttpRequestExecution.execute(request, requestBody)).thenReturn(response);

        assertDoesNotThrow(() -> loggingClientHttpRequestInterceptor.intercept(request, requestBody, clientHttpRequestExecution));
    }

    @Test
    public void testLoggingWithRequestBodyAndWithPdfResponseContentTypeShouldBeSuccess() throws IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("/"));
        when(request.getHeaders()).thenReturn(requestHeaders);

        byte[] requestBody = """
                {"data": "test"}""".getBytes();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(-1L);
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(response.getHeaders()).thenReturn(responseHeaders);

        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        when(clientHttpRequestExecution.execute(request, requestBody)).thenReturn(response);

        assertDoesNotThrow(() -> loggingClientHttpRequestInterceptor.intercept(request, requestBody, clientHttpRequestExecution));
    }

    @Test
    public void testLoggingResponseWithIOExceptionBeSuccess() throws IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getURI()).thenReturn(URI.create("/"));
        when(request.getHeaders()).thenReturn(requestHeaders);

        byte[] requestBody = """
                {"data": "test"}""".getBytes();

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenThrow(IOException.class);

        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        when(clientHttpRequestExecution.execute(request, requestBody)).thenReturn(response);

        assertDoesNotThrow(() -> loggingClientHttpRequestInterceptor.intercept(request, requestBody, clientHttpRequestExecution));
    }

}
