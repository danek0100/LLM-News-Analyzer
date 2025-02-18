package org.si.parsing.scheduler.service.producer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.si.parsing.scheduler.service.IntegrationTest;
import org.si.parsing.scheduler.service.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class KafkaProducerServiceIT extends IntegrationTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    private String topic = "news-links";

    @BeforeEach
    public void setUp() {
        // Настройка ConsumerFactory с реальной Kafka из контейнера
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);

        // Создание контейнера для слушателя сообщений
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setMessageListener((MessageListener<String, String>) record -> {
            // Проверяем, что сообщение получено
            assertThat(record.value()).contains("url");
            assertThat(record.value()).contains("lastParsedTime");
        });

        // Создание и запуск контейнера сообщений
        MessageListenerContainer
            messageListenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        messageListenerContainer.start();

        // Ждем, пока контейнер завершит настройку
        ContainerTestUtils.waitForAssignment(messageListenerContainer, 1);
    }

    @Test
    public void testSendLinkToKafka() {
        // Подготовка тестового объекта Link
        Link testLink = new Link("https://test.com", LocalDateTime.now());

        // Отправка ссылки в Kafka и проверка через StepVerifier
        StepVerifier.create(kafkaProducerService.sendLinkToKafka(testLink))
            .expectSubscription()  // Ожидаем начало подписки
            .expectComplete()      // Ожидаем завершение операции (успешная отправка)
            .verify();            // Проверка завершения
    }
}
