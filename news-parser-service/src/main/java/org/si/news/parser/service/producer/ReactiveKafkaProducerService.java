package org.si.news.parser.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

/**
 * Service for sending parsed news to Kafka.
 * <p>
 * This service handles sending messages to Kafka, logging the status of the message send operation,
 * and providing reactive backpressure handling via Reactor.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveKafkaProducerService {

    private final KafkaSender<String, String> kafkaSender;

    @Value("${spring.kafka.nps.raw-news.topic}")
    private String rawNewsTopic;

    /**
     * Sends a parsed news payload to Kafka.
     * <p>
     * This method sends the message to the specified Kafka topic and logs success or failure.
     * In case of failure, the error is logged.
     *
     * @param key the key of the Kafka message
     * @param jsonPayload the parsed news in JSON format to be sent to Kafka
     * @return a Mono that completes when the message is sent or fails
     */
    public Mono<Void> sendToKafka(String key, String jsonPayload) {
        log.info("Sending parsed news to Kafka: key={}", key);

        return kafkaSender
            .send(Mono.just(SenderRecord.create(new ProducerRecord<>(rawNewsTopic, key, jsonPayload), key)))
            .doOnNext(result -> log.info("Successfully sent to Kafka: key={}, topic={}", key, rawNewsTopic))
            .doOnError(error -> log.error("Failed to send Kafka message: key={}, error={}", key, error.getMessage()))
            .then();
    }

    void setRawNewsTopic(String rawNewsTopic) {
        this.rawNewsTopic = rawNewsTopic;
    }
}
