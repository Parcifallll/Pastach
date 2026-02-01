package com.example.Pastach.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:kafka:9092}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        log.info("Creating Kafka ProducerFactory with bootstrap servers: {}", bootstrapServers);
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        log.info("Creating KafkaTemplate bean");
        return new KafkaTemplate<>(producerFactory());
    }

    // topic: pastach.posts (3 partitions)
    @Bean
    public NewTopic postsTopic() {
        log.info("Creating topic: pastach.posts (3 partitions)");
        return TopicBuilder.name("pastach.posts")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // topic: pastach.reactions (3 partitions)
    @Bean
    public NewTopic reactionsTopic() {
        log.info("Creating topic: pastach.reactions (3 partitions)");
        return TopicBuilder.name("pastach.reactions")
                .partitions(3)
                .replicas(1)
                .build();
    }
}