package org.si.news.parser.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.si.news.parser.service.dto.ArticleDto;
import org.si.news.parser.service.dto.LinkDto;
import org.si.news.parser.service.parser.NewsParser;
import org.si.news.parser.service.parser.ParserFactory;
import org.si.news.parser.service.producer.ReactiveKafkaProducerService;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LinkProcessingServiceTest {

    @Mock
    private ParserFactory parserFactory;

    @Mock
    private ReactiveKafkaProducerService kafkaProducerService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private NewsParser newsParser;

    @InjectMocks
    private LinkProcessingService linkProcessingService;

    private LinkDto linkDto;

    @BeforeEach
    void setUp() {
        linkDto = new LinkDto("https://news-mediator.tradingview.com/news-flow/v1/news", LocalDateTime.now().minusDays(1));
    }

    @Test
    void testProcessLink_Success() {
        // Подготовка
        ArticleDto article = new ArticleDto("Test Article", LocalDateTime.now());
        Flux<ArticleDto> articleFlux = Flux.just(article);

        // Мокируем ParserFactory
        when(parserFactory.getParser(anyString())).thenReturn(newsParser);
        when(newsParser.parse(anyString(), any())).thenReturn(articleFlux);

        // Мокируем sendToKafka с использованием doReturn() для избежания проблемы strict stubbing
        doReturn(Mono.empty()).when(kafkaProducerService).sendToKafka(eq("Test Article"), any());

        // Тест
        Mono<Void> result = linkProcessingService.processLink(linkDto);

        // Проверка
        StepVerifier.create(result)
            .expectComplete()
            .verify();

        // Убедимся, что Kafka producer был вызван с правильными аргументами
        verify(kafkaProducerService, times(1)).sendToKafka(eq("Test Article"), any());
    }

    @Test
    void testProcessLink_Failure_SerializeError() {
        // Подготовка
        ArticleDto article = new ArticleDto("Test Article", LocalDateTime.now());
        Flux<ArticleDto> articleFlux = Flux.just(article);

        when(parserFactory.getParser(anyString())).thenReturn(newsParser);
        when(newsParser.parse(anyString(), any())).thenReturn(articleFlux);
        when(kafkaProducerService.sendToKafka(eq("Test Article"), anyString()))
            .thenReturn(Mono.error(new RuntimeException("Kafka error")));

        // Тест
        Mono<Void> result = linkProcessingService.processLink(linkDto);

        // Проверка
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void testParseLink_Success() {
        // Подготовка
        ArticleDto article = new ArticleDto("Test Article", LocalDateTime.now());
        Flux<ArticleDto> articleFlux = Flux.just(article);

        when(parserFactory.getParser(anyString())).thenReturn(newsParser);
        when(newsParser.parse(anyString(), any())).thenReturn(articleFlux);

        // Тест
        Flux<ArticleDto> result = linkProcessingService.parseLink(linkDto);

        // Проверка
        StepVerifier.create(result)
            .expectNextMatches(articleDto -> "Test Article".equals(articleDto.title()))
            .expectComplete()
            .verify();
    }

    @Test
    void testParseLink_Failure() {
        // Подготовка
        when(parserFactory.getParser(anyString())).thenReturn(newsParser);
        when(newsParser.parse(anyString(), any())).thenReturn(Flux.error(new RuntimeException("Parse error")));

        // Тест
        Flux<ArticleDto> result = linkProcessingService.parseLink(linkDto);

        // Проверка
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }
}
