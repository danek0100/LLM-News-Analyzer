package org.si.parsing.scheduler.service.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.si.parsing.scheduler.service.model.Link;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Service for managing links stored in Redis.
 * Provides methods to add, update, delete, and retrieve links.
 */
@RequiredArgsConstructor
@Service
public class LinkService {

    private final ReactiveRedisOperations<String, Link> reactiveRedisTemplate;

    /**
     * Adds a new link to Redis with the current timestamp.
     *
     * @param url The URL of the link to be added.
     * @return A {@link Mono} containing the added {@link Link}.
     */
    public Mono<Link> addLink(String url) {
        Link link = new Link(url, LocalDateTime.now());
        return reactiveRedisTemplate.opsForValue().set(url, link)
            .thenReturn(link);
    }

    /**
     * Retrieves a link from Redis by its URL.
     *
     * @param url The URL of the link to retrieve.
     * @return A {@link Mono} containing the {@link Link}, or empty if not found.
     */
    public Mono<Link> getLinkByUrl(String url) {
        return reactiveRedisTemplate.opsForValue().get(url);
    }

    /**
     * Updates the "lastChecked" timestamp of an existing link.
     *
     * @param url The URL of the link to update.
     * @return A {@link Mono} containing the updated {@link Link}.
     */
    public Mono<Link> updateLastChecked(String url) {
        return getLinkByUrl(url)
            .flatMap(existingLink -> {
                Link updateLink = new Link(existingLink.url(), LocalDateTime.now());
                return reactiveRedisTemplate.opsForValue().set(url, updateLink)
                    .thenReturn(updateLink);
            });
    }

    /**
     * Deletes a link from Redis by its URL.
     *
     * @param url The URL of the link to delete.
     * @return A {@link Mono} containing {@code true} if the link was deleted, or {@code false} if not found.
     */
    public Mono<Boolean> deleteLink(String url) {
        return reactiveRedisTemplate.opsForValue().get(url)
            .flatMap(existing -> reactiveRedisTemplate.opsForValue().delete(url)
                .thenReturn(true))
            .switchIfEmpty(Mono.just(false));
    }

    /**
     * Retrieves all links stored in Redis.
     *
     * @return A {@link Flux} containing all {@link Link} objects.
     */
    public Flux<Link> getAllLinks() {
        return reactiveRedisTemplate.scan()
            .flatMap(key -> reactiveRedisTemplate.opsForValue().get(key));
    }
}
