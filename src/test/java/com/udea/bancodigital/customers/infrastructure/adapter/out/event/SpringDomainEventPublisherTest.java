package com.udea.bancodigital.customers.infrastructure.adapter.out.event;

import com.udea.bancodigital.customers.domain.event.ClienteRegistradoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpringDomainEventPublisher")
class SpringDomainEventPublisherTest {
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private SpringDomainEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new SpringDomainEventPublisher(applicationEventPublisher);
    }

    @Test
    @DisplayName("Debe delegar la publicación al ApplicationEventPublisher de Spring")
    void debeDelegarAlApplicationEventPublisher() {
        ClienteRegistradoEvent event = ClienteRegistradoEvent.of(
                UUID.randomUUID(), "juan@banco.com", "Juan Pérez");

        publisher.publish(event);

        ArgumentCaptor<ClienteRegistradoEvent> captor = ArgumentCaptor.forClass(ClienteRegistradoEvent.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue()).isEqualTo(event);
    }

    @Test
    @DisplayName("Debe publicar el evento exactamente una vez")
    void debePublicarUnaVez() {
        ClienteRegistradoEvent event = ClienteRegistradoEvent.of(
                UUID.randomUUID(), "ana@banco.com", "Ana García");

        publisher.publish(event);

        verify(applicationEventPublisher).publishEvent(event);
    }

}
