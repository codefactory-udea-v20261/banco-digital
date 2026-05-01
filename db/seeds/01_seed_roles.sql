-- ═══════════════════════════════════════════════════════════════════════════════
-- SEED 01: Roles (Ejecutar segundo)
-- ═══════════════════════════════════════════════════════════════════════════════

INSERT INTO rol (id, nombre)
VALUES
    (1, 'ADMIN'),
    (2, 'CAJERO'),
    (3, 'CLIENTE'),
    (4, 'AUDITOR')
ON CONFLICT (id) DO NOTHING;

SELECT 'Roles creados exitosamente' as status, COUNT(*) as total FROM rol;
