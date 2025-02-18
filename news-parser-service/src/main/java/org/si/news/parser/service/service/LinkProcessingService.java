package org.si.news.parser.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.si.news.parser.service.dto.ArticleDto;
import org.si.news.parser.service.dto.LinkDto;
import org.si.news.parser.service.parser.ParserFactory;
import org.si.news.parser.service.producer.ReactiveKafkaProducerService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Service for processing the links received and sending the parsed article data to Kafka.
 * This service is responsible for parsing news articles from the provided link
 * and then sending the parsed articles to Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkProcessingService {

    private final ParserFactory parserFactory;
    private final ReactiveKafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    /**
     * Processes the link by parsing the content and sending it to Kafka.
     *
     * @param linkDtoJson the DTO containing the URL of the link to process
     * @return a Mono that indicates completion of the operation
     */
    public Mono<Void> processLink(LinkDto linkDtoJson) {
        log.info("Processing link: {}", linkDtoJson.url());

        return parserFactory.getParser(linkDtoJson.url())
            .parse(linkDtoJson.url(), linkDtoJson.lastParsedTime())
            .flatMap(this::sendToKafka)
            .doOnError(error -> log.error("Failed to process link: {}", linkDtoJson.url(), error))
            .then();
    }

    /**
     * Parses the link and returns Flux of ArticleDto.
     *
     * @param linkDtoJson the DTO containing the URL of the link to process
     * @return a Flux of parsed articles
     */
    public Flux<ArticleDto> parseLink(LinkDto linkDtoJson) {
        log.info("Parsing link: {}", linkDtoJson.url());

        return parserFactory.getParser(linkDtoJson.url())
            .parse(linkDtoJson.url(), linkDtoJson.lastParsedTime())
            .doOnError(error -> log.error("Failed to parse link: {}", linkDtoJson.url(), error));
    }

    /**
     * Sends a parsed article to Kafka.
     *
     * @param article the article to be sent
     * @return a Mono that completes when the message is sent or failed
     */
    private Mono<Void> sendToKafka(ArticleDto article) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(article);
            return kafkaProducerService.sendToKafka(article.title(), jsonPayload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize article: {}", article, e);
            return Mono.error(e);
        }
    }
}
