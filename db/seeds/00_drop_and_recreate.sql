-- ═══════════════════════════════════════════════════════════════════════════════
-- SEED 00: Limpiar datos (Ejecutar primero)
-- ═══════════════════════════════════════════════════════════════════════════════
-- ADVERTENCIA: Este script elimina TODOS los datos. Solo para desarrollo local.

-- Limpiar datos de identity DB (sin eliminar tablas)
DELETE FROM usuario_rol CASCADE;
DELETE FROM usuario CASCADE;
DELETE FROM rol CASCADE;

-- Limpiar datos de core banking DB
DELETE FROM transaccion CASCADE;
DELETE FROM cuenta CASCADE;
DELETE FROM cliente CASCADE;

-- Limpiar datos de audit DB
DELETE FROM evento_auditoria CASCADE;

COMMIT;

SELECT 'Base de datos limpiada correctamente' as status;
