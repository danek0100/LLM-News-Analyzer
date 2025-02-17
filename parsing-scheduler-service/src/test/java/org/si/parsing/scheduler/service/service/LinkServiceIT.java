package org.si.parsing.scheduler.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.si.parsing.scheduler.service.IntegrationTest;
import org.si.parsing.scheduler.service.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@SpringBootTest
public class LinkServiceIT extends IntegrationTest {

    @Autowired
    private LinkService linkService;

    @Autowired
    private ReactiveRedisOperations<String, Link> reactiveRedisTemplate;

    @BeforeEach
    public void setUp() {
        // очистка Redis перед каждым тестом
        reactiveRedisTemplate.opsForValue().delete("http://example.com").subscribe();
        reactiveRedisTemplate.opsForValue().delete("http://example1.com").subscribe();
        reactiveRedisTemplate.opsForValue().delete("http://example2.com").subscribe();
    }

    @Test
    public void testAddLink() {
        String url = "http://example.com";
        Link link = new Link(url, LocalDateTime.now());

        // Тестирование метода addLink
        Mono<Link> result = linkService.addLink(url);

        // Проверяем результат с помощью StepVerifier
        StepVerifier.create(result)
            .expectNextMatches(linkResult -> linkResult.url().equals(url))
            .verifyComplete();
    }

    @Test
    public void testGetLinkByUrl() {
        String url = "http://example.com";
        Link link = new Link(url, LocalDateTime.now());

        // Добавляем ссылку в Redis
        linkService.addLink(url).block(); // Мы блокируем здесь, так как это подготовка для теста

        // Тестирование метода getLinkByUrl
        Mono<Link> result = linkService.getLinkByUrl(url);

        // Проверяем результат с помощью StepVerifier
        StepVerifier.create(result)
            .expectNextMatches(linkResult -> linkResult.url().equals(url))
            .verifyComplete();
    }

    @Test
    public void testUpdateLastChecked() {
        String url = "http://example.com";
        Link existingLink = new Link(url, LocalDateTime.now().minusMinutes(10));
        Link updatedLink = new Link(url, LocalDateTime.now());

        // Добавляем ссылку в Redis
        linkService.addLink(url).block();

        // Тестирование метода updateLastChecked
        Mono<Link> result = linkService.updateLastChecked(url);

        // Проверяем результат с помощью StepVerifier
        StepVerifier.create(result)
            .expectNextMatches(linkResult -> linkResult.url().equals(url))
            .verifyComplete();
    }

    @Test
    public void testDeleteLink() {
        String url = "http://example.com";
        Link link = new Link(url, LocalDateTime.now());

        // Добавляем ссылку в Redis
        linkService.addLink(url).block();

        // Тестирование метода deleteLink
        Mono<Boolean> result = linkService.deleteLink(url);

        // Проверяем результат с помощью StepVerifier
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    public void testGetAllLinks() {
        Link link1 = new Link("http://example1.com", LocalDateTime.now());
        Link link2 = new Link("http://example2.com", LocalDateTime.now());

        // Добавляем ссылки в Redis
        linkService.addLink("http://example1.com").block();
        linkService.addLink("http://example2.com").block();

        // Тестирование метода getAllLinks
        Mono<Void> result = linkService.getAllLinks()
            .doOnNext(link -> {
                // Проверяем, что ссылки возвращаются
                assert(link.url().equals("http://example1.com") || link.url().equals("http://example2.com"));
            })
            .then();

        // Проверяем результат с помощью StepVerifier
        StepVerifier.create(result)
            .verifyComplete();
    }
}
