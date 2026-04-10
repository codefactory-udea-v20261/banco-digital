
-- Cuenta INACTIVA de María para pruebas de HU-06
INSERT INTO cuenta (id, numero_cuenta, cliente_id, tipo_cuenta_id, saldo, estado)
VALUES ('b2000000-0000-0000-0000-000000000010', '0001-0001-0010', 'a1000000-0000-0000-0000-000000000001', 1, 50000.00, 'INACTIVA')
ON CONFLICT (numero_cuenta) DO NOTHING;
