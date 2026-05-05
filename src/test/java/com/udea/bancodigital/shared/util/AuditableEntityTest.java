package com.udea.bancodigital.shared.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AuditableEntity Tests")
class AuditableEntityTest {

    private TestAuditableEntity entity;

    @BeforeEach
    void setUp() {
        entity = new TestAuditableEntity();
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        entity.prePersist();
        assertNotNull(entity.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPrePersist() {
        entity.prePersist();
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void shouldSetCreatedByToSystemWhenNull() {
        entity.setCreatedBy(null);
        entity.prePersist();
        assertEquals("SYSTEM", entity.getCreatedBy());
    }

    @Test
    void shouldSetCreatedByToSystemWhenBlank() {
        entity.setCreatedBy("");
        entity.prePersist();
        assertEquals("SYSTEM", entity.getCreatedBy());
    }

    @Test
    void shouldPreserveCreatedByWhenNotNull() {
        entity.setCreatedBy("user1");
        entity.prePersist();
        assertEquals("user1", entity.getCreatedBy());
    }

    @Test
    void shouldSetUpdatedByToCreatedByOnPrePersist() {
        entity.setCreatedBy("user1");
        entity.prePersist();
        assertEquals("user1", entity.getUpdatedBy());
    }

    @Test
    void shouldSetUpdatedByToSystemWhenCreatedByNull() {
        entity.setCreatedBy(null);
        entity.prePersist();
        assertEquals("SYSTEM", entity.getUpdatedBy());
    }

    @Test
    void shouldUpdateUpdatedAtOnPreUpdate() {
        Instant oldTimestamp = Instant.now().minusSeconds(100);
        entity.setUpdatedAt(oldTimestamp);
        
        entity.preUpdate();
        
        assertNotNull(entity.getUpdatedAt());
        assertTrue(entity.getUpdatedAt().isAfter(oldTimestamp));
    }

    @Test
    void shouldSetUpdatedByToSystemWhenBlankOnPreUpdate() {
        entity.setUpdatedBy("");
        entity.preUpdate();
        assertEquals("SYSTEM", entity.getUpdatedBy());
    }

    @Test
    void shouldPreserveUpdatedByWhenNotNullOnPreUpdate() {
        entity.setUpdatedBy("user2");
        entity.preUpdate();
        assertEquals("user2", entity.getUpdatedBy());
    }

    @Test
    void shouldNotOverwriteCreatedAtOnPreUpdate() {
        Instant createdAt = Instant.now().minusSeconds(1000);
        entity.setCreatedAt(createdAt);
        entity.preUpdate();
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void shouldHandleMultiplePrePersistCalls() {
        entity.setCreatedBy("user1");
        entity.prePersist();
        
        Instant firstCreatedAt = entity.getCreatedAt();
        
        entity.prePersist();
        
        // CreatedAt should not be overwritten if already set
        assertEquals(firstCreatedAt, entity.getCreatedAt());
    }

    // Concrete implementation for testing abstract class
    static class TestAuditableEntity extends AuditableEntity {
        // No additional implementation needed
    }
}
