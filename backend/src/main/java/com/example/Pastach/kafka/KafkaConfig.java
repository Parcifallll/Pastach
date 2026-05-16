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

    @Value("${spring.kafka.bootstrap-servers:localhost:9092,localhost:9093,localhost:9094}")
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

    // topic: pastach.posts (3 partitions, 3 replicas, min.isr=2)
    @Bean
    public NewTopic postsTopic() {
        log.info("Creating topic: pastach.posts (3 partitions, replication=3, min.isr=2)");
        return TopicBuilder.name("pastach.posts")
                .partitions(3)
                .replicas(3)  // 3 brokers
                .config("min.insync.replicas", "2")  // >= 2 brokers must confirm
                .build();
    }

    // topic: pastach.reactions (6 partitions, 3 replicas, min.isr=2)
    @Bean
    public NewTopic reactionsTopic() {
        log.info("Creating topic: pastach.reactions (6 partitions, replication=3, min.isr=2)");
        return TopicBuilder.name("pastach.reactions")
                .partitions(6)
                .replicas(3)  // Production: 3 brokers
                .config("min.insync.replicas", "2")  // >= 2 brokers must confirm
                .build();
    }

    // topic: pastach.recommendations (6 partitions, 3 replicas, min.isr=2)
    @Bean
    public NewTopic recommendationsTopic() {
        log.info("Creating topic: pastach.recommendations (6 partitions, replication=3, min.isr=2)");
        return TopicBuilder.name("pastach.recommendations")
                .partitions(6)
                .replicas(3)  // Production: 3 brokers
                .config("min.insync.replicas", "2")  // >= 2 brokers must confirm
                .build();
    }
}