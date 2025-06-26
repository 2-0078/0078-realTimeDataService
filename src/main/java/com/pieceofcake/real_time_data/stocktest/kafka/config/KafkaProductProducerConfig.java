package com.pieceofcake.real_time_data.stocktest.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProductProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public Map<String, Object> realTimeDataProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

//        props.put(ProducerConfig.ACKS_CONFIG, "0");
//        props.put(ProducerConfig.RETRIES_CONFIG, 3);
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> realTimeDataNotification() {
        return new DefaultKafkaProducerFactory<>(realTimeDataProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> realTimeDatakafkaTemplate() {
        return new KafkaTemplate<>(realTimeDataNotification());
    }
}
