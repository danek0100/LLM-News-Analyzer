package org.si.news.parser.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.si.news.parser.service.IntegrationTest;
import org.si.news.parser.service.dto.ArticleDto;
import org.si.news.parser.service.dto.LinkDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDateTime;

import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

@SpringBootTest
class LinkProcessingControllerIT extends IntegrationTest {

    @Autowired
    private LinkProcessingController linkProcessingController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = bindToController(linkProcessingController).build();
    }

    @Test
    void testParseLink_Success() throws Exception {
        // Подготавливаем тестовые данные
        LinkDto linkDto = new LinkDto("https://alenka.capital/category/lenta_novostey_596/", LocalDateTime.now().minusDays(1));

        // Выполняем POST-запрос с передачей данных в теле
        webTestClient.post()
            .uri("/api/v1/parser/parse")
            .bodyValue(linkDto)
            .exchange()
            .expectStatus().isOk() // Проверка, что статус OK
            .expectHeader().contentType("application/json") // Проверка типа контента
            .expectBodyList(ArticleDto.class); // Проверка, что возвращается список объектов ArticleDto
    }

    @Test
    void testParseLink_Error() throws Exception {
        // Подготавливаем тестовые данные
        LinkDto linkDto = new LinkDto("https://invalid.url", LocalDateTime.now());

        // Выполняем POST-запрос с передачей данных в теле
        webTestClient.post()
            .uri("/api/v1/parser/parse")
            .bodyValue(linkDto)
            .exchange()
            .expectStatus().is5xxServerError() // Ожидаем ошибку 5xx (внутренняя ошибка сервера)
            .expectBody() // Проверка тела ответа
            .isEmpty(); // Тело ответа должно быть пустым (ошибка обработки)
    }
}
