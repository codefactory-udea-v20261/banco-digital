package com.udea.bancodigital.shared.util;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class AuditableEntityTest {

    static class TestEntity extends AuditableEntity {}

    @Test
    void prePersist_SetsDefaults() {
        TestEntity entity = new TestEntity();
        entity.prePersist();

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals("SYSTEM", entity.getCreatedBy());
        assertEquals("SYSTEM", entity.getUpdatedBy());
    }

    @Test
    void prePersist_PreservesExistingValues() {
        TestEntity entity = new TestEntity();
        Instant existing = Instant.now().minusSeconds(100);
        entity.setCreatedAt(existing);
        entity.setCreatedBy("USER1");
        
        entity.prePersist();

        assertEquals(existing, entity.getCreatedAt());
        assertEquals("USER1", entity.getCreatedBy());
        assertEquals("USER1", entity.getUpdatedBy());
    }

    @Test
    void preUpdate_UpdatesTimestamp() throws InterruptedException {
        TestEntity entity = new TestEntity();
        entity.prePersist();
        Instant firstUpdate = entity.getUpdatedAt();
        
        Thread.sleep(10);
        entity.preUpdate();
        
        assertTrue(entity.getUpdatedAt().isAfter(firstUpdate));
    }
    
    @Test
    void preUpdate_SetsDefaultAuditorIfMissing() {
        TestEntity entity = new TestEntity();
        entity.setUpdatedBy(null);
        
        entity.preUpdate();
        
        assertEquals("SYSTEM", entity.getUpdatedBy());
    }
}
