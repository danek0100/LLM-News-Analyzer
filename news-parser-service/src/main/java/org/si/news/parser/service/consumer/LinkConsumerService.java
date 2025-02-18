package org.si.news.parser.service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.si.news.parser.service.dto.LinkDto;
import org.si.news.parser.service.service.LinkProcessingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

/**
 * Service for consuming Kafka messages containing links, processing them,
 * and acknowledging the offset after successful processing.
 * <p>
 * This service listens to Kafka messages, deserializes them into LinkDto objects,
 * processes the links, and acknowledges the offset for successful processing.
 * If there are errors during deserialization or processing, they are logged appropriately.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkConsumerService {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final LinkProcessingService linkProcessingService;
    private final ObjectMapper objectMapper;

    /**
     * Starts consuming Kafka messages once the service is initialized.
     * It subscribes to the Kafka consumer and processes each message.
     */
    @PostConstruct
    public void startConsuming() {
        kafkaReceiver.receive()
            .flatMap(this::processMessage)
            .doOnError(error -> log.error("Kafka Consumer Error", error))
            .subscribe();
    }

    /**
     * Processes each Kafka message, deserializes it into a LinkDto,
     * and delegates the processing to LinkProcessingService.
     * If processing is successful, the message offset is acknowledged.
     *
     * @param message the received Kafka message containing the link message
     * @return a Mono that completes when the processing is done
     */
    private Mono<Void> processMessage(ReceiverRecord<String, String> message) {
        log.info("Received Kafka message: key={}", message.key());

        try {
            LinkDto linkDto = objectMapper.readValue(message.value(), LinkDto.class);
            return linkProcessingService.processLink(linkDto)
                .doOnSuccess(success -> message.receiverOffset().acknowledge());
        } catch (JsonProcessingException e) {
            log.error("Error parsing Kafka message: {}", message.value(), e);
            return Mono.empty();
        }
    }
}
