package com.leknarm.boilerplate.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        log.info("Sending request traceparent: {}", request.getHeaders().get("traceparent"));
        ClientHttpResponse response = execution.execute(request, body);
        log.info("Recieved response traceparent: {}", request.getHeaders().get("traceparent"));
        logResponse(request, response);
        return response;
    }

    protected void logRequest(HttpRequest request, byte[] body) {
        StringBuilder builder = new StringBuilder("Sending request: curl -X ");
        builder.append(request.getMethod()).append(" '").append(request.getURI()).append("' ");
        HttpHeaders requestHeaders = request.getHeaders();
        if (body.length > 0 && hasTextBody(requestHeaders)) {
            String bodyText = new String(body, StandardCharsets.UTF_8);
            bodyText = bodyText.replaceAll("\n", Strings.EMPTY);
            MultiValueMap<String, String> headers = request.getHeaders();
            headers.remove("Content-Length");
            headers.forEach((key, value) -> builder.append("-H '").append(key).append(": ").append(String.join(",", value)).append("' "));
            builder.append("-d '").append(bodyText).append("'");
        }
        log.info(builder.toString());
    }

    protected void logResponse(HttpRequest request, ClientHttpResponse response) {

        try {
            StringBuilder builder = new StringBuilder("Received \"");
            builder.append(response.getStatusCode().value()).append(" ").append(response.getStatusText()).append("\" ");
            builder.append("response for ").append(request.getMethod()).append(" ");
            builder.append("request to ").append(request.getURI()).append(" ");
            HttpHeaders responseHeaders = response.getHeaders();
            long contentLength = responseHeaders.getContentLength();
            if (contentLength != 0) {
                if (hasTextBody(responseHeaders)) {
                    String bodyText = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                    bodyText = bodyText.replaceAll("\n", Strings.EMPTY);
                    bodyText = bodyText.replaceAll("\\s+", " ");
                    builder.append(" ").append(bodyText).append(" ");
                } else {
                    if (contentLength == -1) {
                        builder.append(" with content of unknown length");
                    } else {
                        builder.append(" with content of length ").append(contentLength);
                    }
                    MediaType contentType = responseHeaders.getContentType();
                    if (contentType != null) {
                        builder.append(" and content type ").append(contentType);
                    } else {
                        builder.append(" and unknown content type");
                    }
                }
            }
            log.info(builder.toString());
        } catch (IOException e) {
            log.warn("Failed to log response for {} request to {}", request.getMethod(), request.getURI(), e);
        }

    }

    protected boolean hasTextBody(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            if ("text".equals(contentType.getType())) {
                return true;
            }
            String subtype = contentType.getSubtype();
            return "xml".equals(subtype) || "json".equals(subtype) || subtype.endsWith("+xml") || subtype.endsWith("+json");
        }
        return false;
    }

}
