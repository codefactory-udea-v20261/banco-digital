-- ═══════════════════════════════════════════════════════════════════════════════
-- SEED 05: Cuentas Bancarias
-- ═══════════════════════════════════════════════════════════════════════════════
-- Referencia: tipo_cuenta_id
--   1 = AHORRO
--   2 = CORRIENTE

INSERT INTO cuenta (
    id,
    numero_cuenta,
    cliente_id,
    tipo_cuenta_id,
    saldo,
    estado,
    fecha_apertura,
    created_by,
    updated_by
)
VALUES
    -- Cuenta 1: María González - Ahorros - Saldo alto
    (
        'b1000000-0000-0000-0000-000000000001',
        '0001-0001-0001',
        'a1000000-0000-0000-0000-000000000001',
        1,
        1500000.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cuenta 2: Carlos Rodríguez - Corriente - Saldo medio
    (
        'b1000000-0000-0000-0000-000000000002',
        '0001-0001-0002',
        'a1000000-0000-0000-0000-000000000002',
        2,
        250000.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cuenta 3: Valentina Martínez - Ahorros - Saldo bajo
    (
        'b1000000-0000-0000-0000-000000000003',
        '0001-0001-0009',
        'a1000000-0000-0000-0000-000000000003',
        1,
        50000.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cuenta 4: Andrés Castro - Corriente - Saldo cero
    (
        'b1000000-0000-0000-0000-000000000004',
        '0002-0001-0001',
        'a1000000-0000-0000-0000-000000000004',
        2,
        0.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cuenta 5: Luisa Herrera - Ahorros - Saldo muy alto
    (
        'b1000000-0000-0000-0000-000000000005',
        '0003-0001-0001',
        'a1000000-0000-0000-0000-000000000005',
        1,
        3000000.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cuenta 6: María González - Segunda cuenta (Corriente)
    (
        'b1000000-0000-0000-0000-000000000006',
        '0001-0002-0001',
        'a1000000-0000-0000-0000-000000000001',
        2,
        500000.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    ),
    -- Cuenta 7: Carlos Rodríguez - Segunda cuenta (Ahorros)
    (
        'b1000000-0000-0000-0000-000000000007',
        '0002-0002-0001',
        'a1000000-0000-0000-0000-000000000002',
        1,
        750000.00,
        'ACTIVA',
        CURRENT_DATE,
        'SYSTEM',
        'SYSTEM'
    )
ON CONFLICT (numero_cuenta) DO NOTHING;

SELECT 'Cuentas creadas exitosamente' as status, COUNT(*) as total FROM cuenta;
