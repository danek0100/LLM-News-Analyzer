package org.si.parsing.scheduler.service.service;

import lombok.RequiredArgsConstructor;
import org.si.parsing.scheduler.service.producer.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Service
public class SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    private final LinkService linkService;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Scheduled method that processes links every minute.
     * It sends the links to Kafka and updates their "lastChecked" timestamp in Redis.
     * This method is fully asynchronous, with non-blocking operations for Kafka and Redis interactions.
     */
    @Scheduled(fixedRate = 60000)
    public void processLinks() {
        linkService.getAllLinks()
            .publishOn(Schedulers.boundedElastic())
            .flatMap(link -> kafkaProducerService.sendLinkToKafka(link)
                .then(linkService.updateLastChecked(link.url())))
            .doOnNext(link -> LOGGER.info("Successfully processed link: {}", link.url()))
            .doOnError(error -> LOGGER.error("Error processing links: {}", error.getMessage()))
            .subscribe();
    }
}
