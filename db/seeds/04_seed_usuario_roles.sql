-- ═══════════════════════════════════════════════════════════════════════════════
-- SEED 04: Asignar Roles a Usuarios
-- ═══════════════════════════════════════════════════════════════════════════════

INSERT INTO usuario_rol (usuario_id, rol_id)
VALUES
    -- cliente@banco.com → CLIENTE (rol_id = 3)
    ('c3000000-0000-0000-0000-000000000004', 3),
    
    -- asesor@banco.com → CAJERO (rol_id = 2)
    ('c3000000-0000-0000-0000-000000000000', 2),
    
    -- admin@banco.com → ADMIN (rol_id = 1)
    ('c3000000-0000-0000-0000-000000000001', 1),
    
    -- auditor@banco.com → AUDITOR (rol_id = 4)
    ('c3000000-0000-0000-0000-000000000003', 4)
ON CONFLICT (usuario_id, rol_id) DO NOTHING;

SELECT 'Roles asignados exitosamente' as status, COUNT(*) as total FROM usuario_rol;
