package org.si.parsing.scheduler.service.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;


/**
 * Configuration class for setting up a Kafka producer using Reactor Kafka.
 * <p>
 * This class initializes and configures a reactive Kafka sender (`KafkaSender`)
 * that can be used to send messages to Kafka topics within a reactive pipeline.
 * <p>
 */
@Configuration
public class KafkaConfig {

    /**
     * The Kafka bootstrap server(s) to connect to.
     * This property is injected from the application configuration.
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Configures and returns a KafkaSender bean for sending messages to Kafka.
     * <p>
     * This method configures the Kafka sender with essential properties like:
     * <ul>
     *   <li>Bootstrap servers for Kafka connection</li>
     *   <li>Serializer classes for key and value (both are {@link StringSerializer})</li>
     * </ul>
     * Additional properties can be added to the configuration to enhance performance,
     * enable retries, or configure other Kafka producer settings as needed.
     * <p>
     *
     * @return a configured {@link KafkaSender} for producing messages to Kafka topics.
     */
    @Bean
    public KafkaSender<String, String> kafkaSender() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        return KafkaSender.create(senderOptions);
    }
}
