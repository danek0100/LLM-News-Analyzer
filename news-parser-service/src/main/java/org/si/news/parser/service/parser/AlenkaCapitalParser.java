package org.si.news.parser.service.parser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.si.news.parser.service.dto.ArticleDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Parser for AlenkaCapital news site.
 * Extracts news articles and parses their title and publication date.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlenkaCapitalParser implements NewsParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    /**
     * Parses the news articles from the given URL and filters out articles
     * published before the given timestamp.
     *
     * @param url the URL to fetch news from
     * @param lastParsedTime the timestamp used to filter out old articles
     * @return a Flux of {@link ArticleDto} containing the parsed articles
     */
    @Override
    public Flux<ArticleDto> parse(String url, LocalDateTime lastParsedTime) {
        return Mono.fromCallable(() -> fetchNewsFromHtml(url))
            .flatMapMany(Flux::fromIterable)
            .filter(article -> article.publishedAt().isAfter(lastParsedTime))
            .doOnNext(article -> log.info("Parsed new article: {}", article))
            .doOnError(error -> log.error("Error during parsing news from URL: {}", url, error));
    }

    /**
     * Fetches and parses the news articles from the provided URL.
     *
     * @param url the URL to fetch the news from
     * @return a list of {@link ArticleDto} parsed from the page
     */
    private List<ArticleDto> fetchNewsFromHtml(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsItems = doc.select("li.feed__item");

            List<ArticleDto> articles = new ArrayList<>();
            for (Element item : newsItems) {
                String title = item.select("h2.feed__text a").text();
                String dateStr = item.select("time.feed__date").text();

                if (!title.isEmpty() && !dateStr.isEmpty()) {
                    try {
                        LocalDateTime published = LocalDateTime.parse(dateStr, DATE_FORMATTER);
                        articles.add(new ArticleDto(title, published));
                    } catch (Exception e) {
                        log.error("Error parsing date: {} for article: {}", dateStr, title);
                    }
                }
            }
            return articles;
        } catch (IOException e) {
            log.error("Failed to parse AlenkaCapital", e);
            return List.of();
        }
    }
}
