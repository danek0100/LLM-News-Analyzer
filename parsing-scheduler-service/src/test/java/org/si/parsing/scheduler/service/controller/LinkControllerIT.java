package org.si.parsing.scheduler.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.si.parsing.scheduler.service.IntegrationTest;
import org.si.parsing.scheduler.service.model.Link;
import org.si.parsing.scheduler.service.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
public class LinkControllerIT extends IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LinkService linkService;

    private Link link;

    @BeforeEach
    public void setUp() {
        // Подготавливаем ссылку для тестов
        link = new Link("http://example.com", LocalDateTime.now());
        linkService.addLink(link.url()).block(); // Добавляем ссылку в Redis
    }

    @Test
    public void testGetAllLinks() {
        // Проверяем, что все ссылки возвращаются
        webTestClient.get().uri("/api/v1/links/")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Link.class)
            .hasSize(1)
            .contains(link);
    }

    @Test
    public void testGetLinkByUrl() {
        // Проверяем, что ссылка по URL возвращается корректно
        webTestClient.get().uri("/api/v1/links/search?url={url}", link.url())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Link.class)
            .consumeWith(response -> {
                Link responseLink = response.getResponseBody();
                assertEquals(link.url(), responseLink.url());
            });
    }

    @Test
    public void testAddLink() {
        Link newLink = new Link("http://newlink.com", LocalDateTime.now());

        // Проверяем добавление новой ссылки
        webTestClient.post().uri("/api/v1/links/?url=" + newLink.url())
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Link.class)
            .consumeWith(response -> {
                Link responseLink = response.getResponseBody();
                assertEquals(newLink.url(), responseLink.url());
            });

        linkService.deleteLink(newLink.url()).block();
    }

    @Test
    public void testUpdateLastChecked() {
        // Обновляем timestamp ссылки
        webTestClient.put().uri("/api/v1/links/?url=" + link.url())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Link.class)
            .consumeWith(response -> {
                Link responseLink = response.getResponseBody();
                assertEquals(link.url(), responseLink.url());
                // Убедимся, что lastParsedTime обновился
                assert responseLink.lastParsedTime().isAfter(link.lastParsedTime());
            });
    }

    @Test
    public void testDeleteLink() {
        // Проверяем, что ссылка удаляется
        webTestClient.delete().uri("/api/v1/links/?url=" + link.url())
            .exchange()
            .expectStatus().isNoContent();

        // Проверяем, что ссылка была удалена из Redis
        Mono<Link> result = linkService.getLinkByUrl(link.url());
        StepVerifier.create(result)
            .expectNextCount(0)
            .verifyComplete();
    }
}
