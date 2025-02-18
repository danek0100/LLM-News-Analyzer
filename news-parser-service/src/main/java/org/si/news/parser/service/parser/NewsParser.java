package org.si.news.parser.service.parser;

import java.time.LocalDateTime;
import org.si.news.parser.service.dto.ArticleDto;
import reactor.core.publisher.Flux;


/**
 * Interface for news parsers that fetch and parse news articles from a given URL.
 * Implementations should define how articles are parsed and how the last parsed time is utilized.
 */
public interface NewsParser {

    /**
     * Parses the news articles from the provided URL.
     * The method should return all the articles that have been published after the given time.
     *
     * @param url the URL of the news source to parse
     * @param lastParsedTime the timestamp of the last parsed content, used to filter articles
     *                       that have been published after this time
     * @return a Flux of {@link ArticleDto} containing the parsed news articles
     */
    Flux<ArticleDto> parse(String url, LocalDateTime lastParsedTime);
}
