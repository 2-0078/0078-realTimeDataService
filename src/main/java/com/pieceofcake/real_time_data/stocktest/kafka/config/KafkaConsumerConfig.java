package com.pieceofcake.real_time_data.stocktest.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * 공통 consumer config 설정
     */
    @Bean
    public Map<String, Object> realTimeDataConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, String.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    /**
     * Kafka ConsumerFactory 설정
     */
    @Bean
    public ConsumerFactory<String, String> realTimeDataConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                realTimeDataConsumerConfigs(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(String.class, false))
        );
    }

    /**
     * KafkaListenerContainerFactory 설정
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> realTimeDataEventListener() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(realTimeDataConsumerFactory());
        return factory;
    }

}
