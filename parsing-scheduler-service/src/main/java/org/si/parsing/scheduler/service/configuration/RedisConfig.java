package org.si.parsing.scheduler.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.si.parsing.scheduler.service.model.Link;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration class for setting up a reactive Redis template.
 * This configuration sets up the necessary serializers and the ReactiveRedisTemplate
 * to handle Redis operations for storing and retrieving objects of type Link.
 */
@Configuration
public class RedisConfig {

    /**
     * Configures the ReactiveRedisTemplate to perform Redis operations on objects of type Link.
     * It uses a custom Jackson2JsonRedisSerializer to serialize and deserialize Link objects
     * into JSON format, while ensuring Java Time objects are handled properly.
     *
     * @param factory the ReactiveRedisConnectionFactory used to establish a connection to Redis
     * @return a ReactiveRedisOperations instance configured with serializers for Redis operations
     */
    @Bean
    ReactiveRedisOperations<String, Link> redisOperations(ReactiveRedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<Link> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Link.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Link> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Link> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
