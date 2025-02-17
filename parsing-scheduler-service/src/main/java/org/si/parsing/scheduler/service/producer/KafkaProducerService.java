package org.si.parsing.scheduler.service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.si.parsing.scheduler.service.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

/**
 * Reactive Kafka Producer service that sends messages to Kafka topics.
 * This service uses {@link KafkaSender} from Reactor Kafka to produce messages to Kafka.
 */
@RequiredArgsConstructor
@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaSender<String, String> kafkaSender;

    /**
     * The Kafka topic to which links will be sent.
     * This is injected from the application properties.
     */
    @Value("${spring.kafka.news-links.topic}")
    private String topic;

    /**
     * Sends a {@link Link} object to Kafka as a message with URL and lastChecked timestamp.
     * <p>
     * The message is serialized into a JSON string and sent to the configured Kafka topic.
     * </p>
     *
     * @param link the {@link Link} object to be sent to Kafka
     * @return a {@link Mono} indicating the completion of the Kafka send operation
     */
    public Mono<Void> sendLinkToKafka(Link link) {
        String message = String.format("{\"url\":\"%s\", \"lastChecked\":\"%s\"}", link.url(), link.lastChecked());

        SenderRecord<String, String, String> senderRecord = SenderRecord.create(
            new ProducerRecord<>(topic, link.url(), message), link.url()
        );

        Flux<SenderResult<String>> resultFlux = kafkaSender.send(Flux.just(senderRecord));

        return resultFlux
            .doOnNext(senderResult -> LOGGER.info("Message sent successfully: {}", senderResult))
            .doOnError(error -> LOGGER.error("Error sending message to Kafka: {}", error.getMessage()))
            .then();
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
