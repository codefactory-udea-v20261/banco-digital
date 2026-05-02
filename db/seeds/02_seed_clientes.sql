-- ═══════════════════════════════════════════════════════════════════════════════
-- SEED 02: Clientes de Negocio (Core Banking)
-- ═══════════════════════════════════════════════════════════════════════════════
-- 5 clientes de prueba con datos reales

INSERT INTO cliente (
    id, 
    numero_cedula, 
    primer_nombre, 
    segundo_nombre, 
    primer_apellido, 
    segundo_apellido, 
    email, 
    telefono, 
    fecha_nacimiento, 
    activo,
    created_by,
    updated_by
)
VALUES
    -- Cliente 1: María González
    (
        'a1000000-0000-0000-0000-000000000001',
        '1001234567',
        'María',
        NULL,
        'González',
        'López',
        'maria.gonzalez@test.com',
        '+57-300-123-4501',
        '1980-05-15',
        TRUE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cliente 2: Carlos Rodríguez
    (
        'a1000000-0000-0000-0000-000000000002',
        '1012345678',
        'Carlos',
        'Miguel',
        'Rodríguez',
        'Pérez',
        'carlos.rodriguez@test.com',
        '+57-300-234-5602',
        '1985-08-22',
        TRUE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cliente 3: Valentina Martínez
    (
        'a1000000-0000-0000-0000-000000000003',
        '1023456789',
        'Valentina',
        'Aurora',
        'Martínez',
        'Gómez',
        'valentina.m@test.com',
        '+57-300-345-6703',
        '1990-03-10',
        TRUE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cliente 4: Andrés Castro
    (
        'a1000000-0000-0000-0000-000000000004',
        '1034567890',
        'Andrés',
        'Felipe',
        'Castro',
        'Silva',
        'andres.castro@test.com',
        '+57-300-456-7804',
        '1988-12-05',
        TRUE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cliente 5: Luisa Herrera
    (
        'a1000000-0000-0000-0000-000000000005',
        '1045678901',
        'Luisa',
        'Fernanda',
        'Herrera',
        'Morales',
        'luisa.herrera@test.com',
        '+57-300-567-8905',
        '1992-07-20',
        TRUE,
        'SYSTEM',
        'SYSTEM'
    )
ON CONFLICT (numero_cedula) DO NOTHING;

SELECT 'Clientes creados exitosamente' as status, COUNT(*) as total FROM cliente;
