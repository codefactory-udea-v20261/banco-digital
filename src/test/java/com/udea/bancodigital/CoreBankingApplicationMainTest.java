package com.udea.bancodigital;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class CoreBankingApplicationMainTest {

    @Test
    void main_givenArgs_invokesSpringApplicationRun() {
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
            springApplicationMock
                    .when(() -> SpringApplication.run(eq(CoreBankingApplication.class), any(String[].class)))
                    .thenReturn(context);

            CoreBankingApplication.main(new String[]{"--spring.main.web-application-type=none"});

            springApplicationMock.verify(() ->
                    SpringApplication.run(eq(CoreBankingApplication.class), any(String[].class))
            );
        }
    }
}
