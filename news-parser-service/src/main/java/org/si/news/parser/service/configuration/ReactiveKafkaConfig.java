package org.si.news.parser.service.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;


/**
 * Configuration class for setting up Kafka consumer and producer beans.
 * This configuration is tailored for a reactive application using Reactor Kafka.
 * <p>
 * It includes the necessary properties for consuming and producing messages
 * to Kafka topics, as well as creating the required KafkaReceiver and KafkaSender beans.
 */
@Configuration
public class ReactiveKafkaConfig {

    @Value("${spring.kafka.pss.bootstrap-servers}")
    private String pssBootstrapServers;

    @Value("${spring.kafka.pss.news-links.topic}")
    private String pssTopic;

    @Value("${spring.kafka.pss.news-links.consumer-group}")
    private String pssGroupId;

    @Value("${spring.kafka.nps.bootstrap-servers}")
    private String npsBootstrapServers;

    @Value("${spring.kafka.nps.raw-news.topic}")
    private String npsTopic;

    /**
     * Configures the ReceiverOptions bean for Kafka consumer.
     *
     * @return ReceiverOptions instance with Kafka consumer configurations.
     */
    @Bean
    public ReceiverOptions<String, String> kafkaReceiverOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, pssBootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, pssGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return ReceiverOptions.<String, String>create(props)
                .subscription(Collections.singleton(pssTopic));
    }

    /**
     * Creates a KafkaReceiver bean for consuming messages from Kafka topics.
     *
     * @param options ReceiverOptions containing configuration for the Kafka consumer.
     * @return KafkaReceiver instance for consuming messages.
     */
    @Bean
    public KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> options) {
        return KafkaReceiver.create(options);
    }

    /**
     * Configures the SenderOptions bean for Kafka producer.
     *
     * @return SenderOptions instance with Kafka producer configurations.
     */
    @Bean
    public SenderOptions<String, String> kafkaSenderOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, npsBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        return SenderOptions.create(props);
    }

    /**
     * Creates a KafkaSender bean for producing messages to Kafka topics.
     *
     * @param senderOptions SenderOptions containing configuration for the Kafka producer.
     * @return KafkaSender instance for producing messages.
     */
    @Bean
    public KafkaSender<String, String> kafkaSender(SenderOptions<String, String> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}
