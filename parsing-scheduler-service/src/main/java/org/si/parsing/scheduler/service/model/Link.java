package org.si.parsing.scheduler.service.model;

import java.time.LocalDateTime;
import org.springframework.data.redis.core.RedisHash;

/**
 * Represents a Link entity stored in Redis with a URL and a timestamp indicating when it was last checked.
 */
@RedisHash("Link")
public record Link(String url, LocalDateTime lastParsedTime) {}
