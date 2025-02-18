package org.si.news.parser.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NewsParserServiceApplicationIT extends IntegrationTest {

    @Test
    void contextLoads() {
        // Проверка загрузки контекста
    }
}
