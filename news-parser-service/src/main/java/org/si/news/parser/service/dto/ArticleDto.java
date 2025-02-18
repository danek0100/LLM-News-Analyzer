package org.si.news.parser.service.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing a news article.
 * This includes the article title and the time of publication.
 * It is used for transferring article data between different layers of the application.
 */
public record ArticleDto(String title, LocalDateTime publishedAt) {}
