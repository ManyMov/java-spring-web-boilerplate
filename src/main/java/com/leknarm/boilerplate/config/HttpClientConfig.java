package com.leknarm.boilerplate.config;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
    private static final int DEFAULT_REQUEST_TIMEOUT = 60 * 1000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 60 * 1000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 60 * 1000;
    private static final int DEFAULT_MAX_REQUEST_CONNECTION = 1000;
    private static final int DEFAULT_MAX_REQUEST_CONNECTION_PER_ROUTE = 200;

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HttpHeaders.KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.next();
                String param = he.getName();
                String value = he.getValue();

                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return TimeValue.ofSeconds(Long.parseLong(value));
                }
            }
            return TimeValue.ofMilliseconds(DEFAULT_KEEP_ALIVE_TIME_MILLIS);
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(DEFAULT_REQUEST_TIMEOUT))
                .setConnectTimeout(Timeout.ofMilliseconds(DEFAULT_CONNECT_TIMEOUT))
                .setResponseTimeout(Timeout.ofMilliseconds(DEFAULT_SOCKET_TIMEOUT))
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(DEFAULT_MAX_REQUEST_CONNECTION);
        poolingConnectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_REQUEST_CONNECTION_PER_ROUTE);
        return poolingConnectionManager;
    }

}
