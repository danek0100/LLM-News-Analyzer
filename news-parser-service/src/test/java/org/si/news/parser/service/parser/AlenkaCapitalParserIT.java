package org.si.news.parser.service.parser;

import org.junit.jupiter.api.Test;
import org.si.news.parser.service.dto.ArticleDto;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlenkaCapitalParserIT {

    private final AlenkaCapitalParser parser = new AlenkaCapitalParser();

    @Test
    public void testParse_realSite_shouldReturnArticles() {
        // Используем реальный URL
        String url = "https://alenka.capital/category/lenta_novostey_596/";

        // Ставим время, с которого начнём парсить (например, текущая дата)
        LocalDateTime lastParsedTime = LocalDateTime.now().minusDays(1); // Например, последние 24 часа

        // Получаем парсинг данных с сайта
        Flux<ArticleDto> articles = parser.parse(url, lastParsedTime);

        // Проверяем, что хотя бы одна статья была найдена
        StepVerifier.create(articles)
            .expectNextMatches(article -> article.title() != null && !article.title().isEmpty())
            .expectNextMatches(article -> article.publishedAt() != null)
            .thenConsumeWhile(article -> article.title() != null && !article.title().isEmpty()) // Допустим, мы ожидаем, что будет хотя бы одна статья и поток завершится
            .verifyComplete();

        // Проверяем, что хотя бы одна статья была опубликована после lastParsedTime
        articles.subscribe(article -> assertTrue(article.publishedAt().isAfter(lastParsedTime),
            "Article published date is older than lastParsedTime: " + article));
    }
}
