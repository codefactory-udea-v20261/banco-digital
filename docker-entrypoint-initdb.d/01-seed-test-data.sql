-- ============================================================================
-- SEED DATA FOR MICROSERVICES TESTING
-- ============================================================================
-- This script inserts test data into the three microservices databases
-- to enable local development and testing with Postman
-- ============================================================================

-- ============================================================================
-- BANCO DIGITAL CORE - Test Client and Account
-- ============================================================================
\c banco_digital_core

-- Insert test client
INSERT INTO cliente (
  numero_cedula, 
  primer_nombre, 
  segundo_nombre, 
  primer_apellido, 
  segundo_apellido, 
  email, 
  telefono, 
  fecha_nacimiento, 
  activo
) VALUES (
  '1234567890',
  'Juan',
  'Carlos',
  'Pérez',
  'García',
  'cliente@banco.com',
  '3101234567',
  '1990-05-15',
  true
)
ON CONFLICT DO NOTHING;

-- Insert test account for the client (using tipo_cuenta_id reference)
INSERT INTO cuenta (
  numero_cuenta,
  cliente_id,
  tipo_cuenta_id,
  saldo,
  estado
) VALUES (
  '1000000001',
  (SELECT id FROM cliente WHERE email = 'cliente@banco.com'),
  1,  -- Assuming AHORROS has ID 1, adjust if needed
  1000000.00,
  'ACTIVA'
)
ON CONFLICT DO NOTHING;

-- ============================================================================
-- BANCO DIGITAL IDENTITY - Test User and Roles
-- ============================================================================
\c banco_digital_identity

-- Insert test roles (must have explicit IDs for references)
-- Check existing roles first - if they exist, skip
INSERT INTO rol (id, nombre)
VALUES 
  (1, 'CLIENTE'),
  (2, 'ADMIN')
ON CONFLICT DO NOTHING;

-- Insert test user with password: Temp1234!
-- Hash: $2b$12$9m1/h7yIdn4zIWiVgzf4veGZ3TVbrE1FBNrI8IJ.Ryq5Na9jbEclW
INSERT INTO usuario (
  username,
  password_hash,
  activo,
  mfa_activo,
  intentos_fallidos,
  created_at
) VALUES (
  'cliente@banco.com',
  '$2b$12$9m1/h7yIdn4zIWiVgzf4veGZ3TVbrE1FBNrI8IJ.Ryq5Na9jbEclW',
  true,
  false,
  0,
  NOW()
)
ON CONFLICT DO NOTHING;

-- Link user to CLIENTE role
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT 
  u.id,
  r.id
FROM usuario u
CROSS JOIN rol r
WHERE u.username = 'cliente@banco.com'
  AND r.nombre = 'CLIENTE'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_rol ur2
    WHERE ur2.usuario_id = u.id AND ur2.rol_id = r.id
  );

-- ============================================================================
-- VERIFICATION QUERIES (For debugging)
-- ============================================================================
-- To verify seed data was inserted, run:
--
-- \c banco_digital_core
-- SELECT id, numero_cedula, primer_nombre, email FROM cliente;
--
-- SELECT numero_cuenta, tipo_cuenta, saldo FROM cuenta;
--
-- \c banco_digital_identity
-- SELECT id, username, activo FROM usuario;
--
-- SELECT ur.usuario_id, ur.rol_id, r.nombre 
-- FROM usuario_rol ur
-- JOIN rol r ON ur.rol_id = r.id;
-- ============================================================================
