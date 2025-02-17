package org.si.parsing.scheduler.service.service;

import org.junit.jupiter.api.Test;
import org.si.parsing.scheduler.service.IntegrationTest;
import org.si.parsing.scheduler.service.model.Link;
import org.si.parsing.scheduler.service.producer.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@SpringBootTest
public class SchedulerServiceIT extends IntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceIT.class);

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Test
    public void testProcessLinks() {
        // Создаем объект Link
        Link link = new Link("http://example.com", LocalDateTime.now());

        // Добавляем ссылку в Redis
        linkService.addLink(link.url()).block();

        // Выполняем метод processLinks
        schedulerService.processLinks();

        // Подтверждаем, что ссылка была отправлена в Kafka и обновлена в Redis
        Mono<Link> result = linkService.getLinkByUrl(link.url());

        // Проверяем, что ссылка успешно обновлена и присутствует в базе данных
        StepVerifier.create(result)
            .expectNextMatches(linkResult -> linkResult.url().equals(link.url()))
            .verifyComplete();
    }
}
