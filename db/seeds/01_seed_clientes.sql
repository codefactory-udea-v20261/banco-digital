-- ════════════════════════════════════════════════════════════
-- SEED: Datos de prueba — Clientes
-- Uso: Solo para entornos LOCAL y TEST. NUNCA en producción.
-- Ejecutar: psql -U postgres -d banco_digital -f 01_seed_clientes.sql
-- ════════════════════════════════════════════════════════════

INSERT INTO cliente (id, numero_cedula, primer_nombre, segundo_nombre, primer_apellido,
                     segundo_apellido, email, telefono, fecha_nacimiento, activo)
VALUES
    ('a1000000-0000-0000-0000-000000000001', '1234567890', 'María',    'José',    'González', 'Pérez',   'maria.gonzalez@test.com',   '3001234567', '1990-05-15', TRUE),
    ('a1000000-0000-0000-0000-000000000002', '0987654321', 'Carlos',   NULL,      'Rodríguez','García',  'carlos.rodriguez@test.com', '3109876543', '1985-11-22', TRUE),
    ('a1000000-0000-0000-0000-000000000003', '1122334455', 'Valentina','Sofía',   'Martínez', 'López',   'valentina.m@test.com',      '3201122334', '1998-03-08', TRUE),
    ('a1000000-0000-0000-0000-000000000004', '5544332211', 'Andrés',   'Felipe',  'Castro',   'Ramos',   'andres.castro@test.com',    '3155544332', '1975-07-30', TRUE),
    ('a1000000-0000-0000-0000-000000000005', '9988776655', 'Luisa',    NULL,      'Herrera',  'Moreno',  'luisa.herrera@test.com',    '3209988776', '2000-01-12', FALSE)  -- cliente inactivo
ON CONFLICT (numero_cedula) DO NOTHING;
