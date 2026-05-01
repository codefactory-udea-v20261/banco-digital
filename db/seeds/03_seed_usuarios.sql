-- ═══════════════════════════════════════════════════════════════════════════════
-- SEED 03: Usuarios de Autenticación (Identity)
-- ═══════════════════════════════════════════════════════════════════════════════
-- Contraseñas (todas BCrypt factor 12):
-- 
-- cliente@banco.com      → Temp1234!
--   Hash: $2b$12$bWEN8uPKuPk7LZUpwlzE2uN8LDz61wXlFoOfIuALHOBu0H.F9Md.m
--
-- asesor@banco.com       → Test1234!
--   Hash: $2b$12$xloEKZpEvdOKbEbvaGFH0.s/dJyWgGXHeTCQrY.yua0e7vLiCur1e
--
-- admin@banco.com        → Test1234!
--   Hash: $2b$12$P3BPsTEh/87IADzcb2ElGuWpp0hC3Ryxas6xZ1hdhSaQCMah4cQzK
--
-- auditor@banco.com      → Test1234!
--   Hash: $2b$12$2l2.aGwPP2Tw.MWCEiYPGuIymT1UYcy2WRdtka8hKFbO6kYKINLQa

INSERT INTO usuario (
    id, 
    username, 
    password_hash, 
    cliente_id, 
    activo, 
    mfa_activo,
    intentos_fallidos,
    bloqueado_hasta
)
VALUES
    -- Usuario 1: Cliente (sin asociación a cliente_id específico)
    (
        'c3000000-0000-0000-0000-000000000004',
        'cliente@banco.com',
        '$2b$12$bWEN8uPKuPk7LZUpwlzE2uN8LDz61wXlFoOfIuALHOBu0H.F9Md.m',
        NULL,
        TRUE,
        FALSE,
        0,
        NULL
    ),
    -- Usuario 2: Asesor/Cajero
    (
        'c3000000-0000-0000-0000-000000000000',
        'asesor@banco.com',
        '$2b$12$xloEKZpEvdOKbEbvaGFH0.s/dJyWgGXHeTCQrY.yua0e7vLiCur1e',
        NULL,
        TRUE,
        FALSE,
        0,
        NULL
    ),
    -- Usuario 3: Admin
    (
        'c3000000-0000-0000-0000-000000000001',
        'admin@banco.com',
        '$2b$12$P3BPsTEh/87IADzcb2ElGuWpp0hC3Ryxas6xZ1hdhSaQCMah4cQzK',
        NULL,
        TRUE,
        FALSE,
        0,
        NULL
    ),
    -- Usuario 4: Auditor
    (
        'c3000000-0000-0000-0000-000000000003',
        'auditor@banco.com',
        '$2b$12$2l2.aGwPP2Tw.MWCEiYPGuIymT1UYcy2WRdtka8hKFbO6kYKINLQa',
        NULL,
        TRUE,
        FALSE,
        0,
        NULL
    )
ON CONFLICT (username) DO NOTHING;

SELECT 'Usuarios creados exitosamente' as status, COUNT(*) as total FROM usuario;
