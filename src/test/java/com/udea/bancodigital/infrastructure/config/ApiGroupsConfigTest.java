package com.udea.bancodigital.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import static org.junit.jupiter.api.Assertions.*;

class ApiGroupsConfigTest {

    @Test
    void testApiGroups() {
        ApiGroupsConfig config = new ApiGroupsConfig();
        assertNotNull(config.customersApi());
        assertNotNull(config.accountsApi());
        assertNotNull(config.transactionsApi());
        assertNotNull(config.transfersApi());
        
        GroupedOpenApi customers = config.customersApi();
        assertEquals("clientes", customers.getGroup());
    }
}
