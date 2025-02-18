package org.si.news.parser.service.dto;

import java.time.LocalDateTime;

/**
 * Represents a link to be processed, including the URL and the time of last processing.
 * This model is used for tracking and filtering links based on the time they were last checked.
 */
public record LinkDto(String url, LocalDateTime lastParsedTime) {}
