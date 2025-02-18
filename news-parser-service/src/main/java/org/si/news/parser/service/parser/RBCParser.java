package org.si.news.parser.service.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.si.news.parser.service.dto.ArticleDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


/**
 * Parses news articles from the RBC website.
 * Converts JSON response into {@link ArticleDto} objects,
 * filtering out articles that were published before the given timestamp.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RBCParser implements NewsParser {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * Fetches the news articles from the given URL and filters out articles published before the specified timestamp.
     *
     * @param url the URL to fetch the news from
     * @param lastParsedTime the timestamp to filter articles that were published after it
     * @return a Flux of {@link ArticleDto} containing the parsed articles
     */
    @Override
    public Flux<ArticleDto> parse(String url, LocalDateTime lastParsedTime) {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .flatMapMany(jsonResponse -> parseJson(jsonResponse, lastParsedTime));
    }

    /**
     * Parses the JSON response from RBC into a Flux of {@link ArticleDto}.
     * Filters out articles that were published before the given timestamp.
     *
     * @param jsonResponse the JSON response body as a string
     * @param lastParsedTime the timestamp to filter articles
     * @return a Flux of parsed articles
     */
    private Flux<ArticleDto> parseJson(String jsonResponse, LocalDateTime lastParsedTime) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.path("items");

            return Flux.fromIterable(items)
                .map(item -> {
                    String title = item.path("title").asText();
                    long timestamp = item.path("publish_date_t").asLong();
                    LocalDateTime published = Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                    return new ArticleDto(title, published);
                })
                .filter(article -> article.publishedAt().isAfter(lastParsedTime))
                .doOnNext(article -> log.info("Parsed new RBC article: {}", article))
                .doOnError(error -> log.error("Error while parsing RBC response", error));
        } catch (Exception e) {
            log.error("Failed to parse RBC response", e);
            return Flux.empty();
        }
    }
}
