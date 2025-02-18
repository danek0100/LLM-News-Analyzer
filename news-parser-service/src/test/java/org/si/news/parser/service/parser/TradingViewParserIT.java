package org.si.news.parser.service.parser;


import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.si.news.parser.service.IntegrationTest;
import org.si.news.parser.service.dto.ArticleDto;
import reactor.core.publisher.Flux;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;


@Slf4j
@SpringBootTest
public class TradingViewParserIT extends IntegrationTest {

    @Autowired
    private TradingViewParser tradingViewParser;

    private static final String URL = "https://news-mediator.tradingview.com/news-flow/v1/news?filter=lang:ru&filter=symbol:RUS:SBER&streaming=true";

    private LocalDateTime lastParsedTime;

    @BeforeEach
    public void setUp() {
        // Устанавливаем время, с которого будем начинать парсинг
        lastParsedTime = LocalDateTime.now().minusYears(5);
    }

    @Test
    public void testParse_shouldReturnArticles() {
        // Вызываем метод parse для получения Flux с новыми статьями
        Flux<ArticleDto> articles = tradingViewParser.parse(URL, lastParsedTime);

        // Проверяем, что хотя бы одна статья была получена и опубликована после lastParsedTime
        StepVerifier.create(articles)
                .expectNextMatches(article -> article.title() != null && !article.title().isEmpty()) // Проверка на наличие title
                .expectNextMatches(article -> article.publishedAt().isAfter(lastParsedTime)) // Проверка на дату публикации
                .thenConsumeWhile(article -> article.title() != null && !article.title().isEmpty()) // Прочитаем все статьи, пока они идут
                .verifyComplete(); // Подтверждаем, что поток завершился
    }

    @Test
    public void testParse_shouldNotReturnOldArticles() {
        // Время старой статьи (до 2024 года)
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);

        // Делаем запрос с временем старой статьи
        Flux<ArticleDto> articles = tradingViewParser.parse(URL, futureDate);

        // Проверяем, что статьи не будут возвращены
        StepVerifier.create(articles)
                .expectNextCount(0) // Не ожидаем старых статей
                .verifyComplete();
    }
}
