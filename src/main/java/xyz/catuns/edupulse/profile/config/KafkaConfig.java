package xyz.catuns.edupulse.profile.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
class KafkaConfig {

    @Bean
    public ProducerFactory<String, SpecificRecord> resumeProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = kafkaProperties.buildProducerProperties(null);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, SpecificRecord> resumeKafkaTemplate(ProducerFactory<String, SpecificRecord> resumeProducerFactory) {
        return new KafkaTemplate<>(resumeProducerFactory);
    }
}