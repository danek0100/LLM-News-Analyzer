package org.si.news.parser.service.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.si.news.parser.service.IntegrationTest;
import org.si.news.parser.service.dto.ArticleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RBCParserIT extends IntegrationTest {

    @Autowired
    private RBCParser rbcParser;

    private static final String URL = "https://www.rbc.ru/search/ajax/?tag=Ozon&project=rbcnews&page=0"; // Пример URL RBC с тегом Ozon

    private LocalDateTime lastParsedTime;

    @BeforeEach
    public void setUp() {
        // Устанавливаем время, с которого будем начинать парсинг
        lastParsedTime = LocalDateTime.now().minusYears(5); // Например, 1 день назад
    }

    @Test
    public void testParse_shouldReturnArticles() {
        // Вызываем метод parse для получения Flux с новыми статьями
        Flux<ArticleDto> articles = rbcParser.parse(URL, lastParsedTime);

        // Проверяем, что хотя бы одна статья была получена и опубликована после lastParsedTime
        StepVerifier.create(articles)
                .expectNextMatches(article -> article.title() != null && !article.title().isEmpty()) // Проверка на наличие title
                .expectNextMatches(article -> article.publishedAt().isAfter(lastParsedTime)) // Проверка на дату публикации
                .thenConsumeWhile(article -> article.title() != null && !article.title().isEmpty()) // Прочитаем все статьи, пока они идут
                .verifyComplete(); // Подтверждаем, что поток завершился

        articles.subscribe(article -> assertTrue(article.publishedAt().isAfter(lastParsedTime),
            "Article published date is older than lastParsedTime: " + article));
    }
}
