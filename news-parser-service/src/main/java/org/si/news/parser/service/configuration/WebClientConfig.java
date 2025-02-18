package org.si.news.parser.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Configuration class for setting up a WebClient bean.
 * The WebClient is used for making HTTP requests in a reactive way.
 * This configuration also adds logging for the requests made through WebClient.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a WebClient bean.
     * This WebClient is configured with a filter that logs the URL of every request.
     *
     * @return the configured WebClient bean
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .filter((request, next) -> {
                log.info("Request: {}", request.url());
                return next.exchange(request);
            }).build();
    }
}
