-- ════════════════════════════════════════════════════════════
-- SEED: Datos de prueba — Usuarios y Roles
-- Contraseñas: BCrypt factor 12 de 'Test1234!'
-- ════════════════════════════════════════════════════════════

INSERT INTO usuario (id, username, password_hash, cliente_id, rol_id, activo, mfa_activo)
VALUES
    -- Asesor bancario / cajero
    ('c3000000-0000-0000-0000-000000000000',
     'asesor@banco.com',
     '$2a$12$xBejBl1pPh02RU.djP3E1ejUcAMBMOzL4egSn4p2fSiAA0pcxfHaO',
     NULL,
     2,   -- CAJERO
     TRUE,
     FALSE),
    -- Admin del sistema (sin cliente asociado)
    ('c3000000-0000-0000-0000-000000000001',
     'admin@banco.com',
     '$2a$12$xBejBl1pPh02RU.djP3E1ejUcAMBMOzL4egSn4p2fSiAA0pcxfHaO',
     NULL,
     1,   -- ADMIN
     TRUE,
     FALSE),
    -- Cliente normal — María González
    ('c3000000-0000-0000-0000-000000000002',
     'maria.gonzalez@test.com',
     '$2a$12$xBejBl1pPh02RU.djP3E1ejUcAMBMOzL4egSn4p2fSiAA0pcxfHaO',
     'a1000000-0000-0000-0000-000000000001',
     3,   -- CLIENTE
     TRUE,
     FALSE),
    -- Auditor
    ('c3000000-0000-0000-0000-000000000003',
     'auditor@banco.com',
     '$2a$12$xBejBl1pPh02RU.djP3E1ejUcAMBMOzL4egSn4p2fSiAA0pcxfHaO',
     NULL,
     4,   -- AUDITOR
     TRUE,
     FALSE)
ON CONFLICT (username) DO NOTHING;

-- RECORDATORIO: La contraseña de todos los usuarios seed es 'Test1234!'
-- Cambiar INMEDIATAMENTE si este script llega a un entorno no local.
