package com.udea.bancodigital.shared.health;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaHealthIndicator")

class KafkaHealthIndicatorTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaHealthIndicator indicator;

    @BeforeEach
    void setUp() {
        indicator = new KafkaHealthIndicator(kafkaTemplate);
    }

    @Nested
    @DisplayName("Kafka disponible")
    class KafkaDisponible {

        @Test
        @DisplayName("Debe retornar UP cuando el mensaje se confirma correctamente")
        void debeRetornarUp() throws Exception {
            RecordMetadata metadata = new RecordMetadata(
                    new TopicPartition("health-check", 0), 0, 0, 0, 0, 0);
            SendResult<String, String> sendResult = new SendResult<>(null, metadata);
            CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);

            when(kafkaTemplate.send(any(org.springframework.messaging.Message.class))).thenReturn(future);

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.UP);
            assertThat(health.getDetails().get("kafka")).isEqualTo("Connected and operational");
        }
    }

    @Nested
    @DisplayName("Kafka no disponible")
    class KafkaNoDisponible {

        @Test
        @DisplayName("Debe retornar OUT_OF_SERVICE cuando hay timeout")
        void debeRetornarOutOfServiceCuandoTimeout() {
            CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
            future.completeExceptionally(new TimeoutException("Timeout"));

            when(kafkaTemplate.send(any(org.springframework.messaging.Message.class))).thenReturn(future);

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
            assertThat(health.getDetails().get("mode")).isEqualTo("graceful-degradation");
        }

        @Test
        @DisplayName("Debe retornar OUT_OF_SERVICE cuando hay error de conexión")
        void debeRetornarOutOfServiceCuandoErrorConexion() {
            CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Broker unavailable"));

            when(kafkaTemplate.send(any(org.springframework.messaging.Message.class))).thenReturn(future);

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
        }
    }

}
