#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════════════
# Script Maestro: Ejecutar todos los seeds en orden correcto
# ═══════════════════════════════════════════════════════════════════════════════

set -e  # Exit on error

echo "════════════════════════════════════════════════════════════════════════════════"
echo "🌱 INICIANDO SEED DE BASE DE DATOS"
echo "════════════════════════════════════════════════════════════════════════════════"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_CONTAINER="banco-digital-db"
DB_USER="postgres"
IDENTITY_DB="banco_digital_identity"
CORE_DB="banco_digital_core"

# Verificar que el contenedor existe
if ! docker ps | grep -q "$DB_CONTAINER"; then
    echo "❌ Error: Contenedor '$DB_CONTAINER' no está corriendo"
    exit 1
fi

echo "✅ Contenedor $DB_CONTAINER encontrado"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# STEP 1: Limpiar datos (opcional)
# ═══════════════════════════════════════════════════════════════════════════════

echo "📋 STEP 1: Limpiando datos previos..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" \
    -d "$IDENTITY_DB" \
    < "$SCRIPT_DIR/00_drop_and_recreate.sql" \
    > /dev/null 2>&1 || true
echo "   ✅ Datos previos eliminados"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# STEP 2: Crear roles (Identity DB)
# ═══════════════════════════════════════════════════════════════════════════════

echo "📋 STEP 2: Creando roles..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" \
    -d "$IDENTITY_DB" \
    < "$SCRIPT_DIR/01_seed_roles.sql"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# STEP 3: Crear clientes (Core Banking DB)
# ═══════════════════════════════════════════════════════════════════════════════

echo "📋 STEP 3: Creando clientes..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" \
    -d "$CORE_DB" \
    < "$SCRIPT_DIR/02_seed_clientes.sql"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# STEP 4: Crear usuarios (Identity DB)
# ═══════════════════════════════════════════════════════════════════════════════

echo "📋 STEP 4: Creando usuarios..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" \
    -d "$IDENTITY_DB" \
    < "$SCRIPT_DIR/03_seed_usuarios.sql"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# STEP 5: Asignar roles a usuarios (Identity DB)
# ═══════════════════════════════════════════════════════════════════════════════

echo "📋 STEP 5: Asignando roles a usuarios..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" \
    -d "$IDENTITY_DB" \
    < "$SCRIPT_DIR/04_seed_usuario_roles.sql"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# STEP 6: Crear cuentas (Core Banking DB)
# ═══════════════════════════════════════════════════════════════════════════════

echo "📋 STEP 6: Creando cuentas..."
docker exec -i "$DB_CONTAINER" psql -U "$DB_USER" \
    -d "$CORE_DB" \
    < "$SCRIPT_DIR/05_seed_cuentas.sql"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# VERIFICATION
# ═══════════════════════════════════════════════════════════════════════════════

echo "════════════════════════════════════════════════════════════════════════════════"
echo "✅ VERIFICACIÓN FINAL"
echo "════════════════════════════════════════════════════════════════════════════════"
echo ""

echo "👥 Usuarios en la BD:"
docker exec "$DB_CONTAINER" psql -U "$DB_USER" -d "$IDENTITY_DB" \
    -c "SELECT username, activo FROM usuario ORDER BY username;" 2>/dev/null

echo ""
echo "�� Clientes en la BD:"
docker exec "$DB_CONTAINER" psql -U "$DB_USER" -d "$CORE_DB" \
    -c "SELECT primer_nombre, primer_apellido, email FROM cliente ORDER BY primer_nombre;" 2>/dev/null

echo ""
echo "🏦 Cuentas en la BD:"
docker exec "$DB_CONTAINER" psql -U "$DB_USER" -d "$CORE_DB" \
    -c "SELECT numero_cuenta, saldo FROM cuenta ORDER BY numero_cuenta;" 2>/dev/null

echo ""
echo "════════════════════════════════════════════════════════════════════════════════"
echo "✅ SEED COMPLETADO EXITOSAMENTE"
echo "════════════════════════════════════════════════════════════════════════════════"
echo ""
echo "Credenciales de prueba:"
echo "  📧 cliente@banco.com       → Temp1234!"
echo "  📧 asesor@banco.com        → Test1234!"
echo "  📧 admin@banco.com         → Test1234!"
echo "  📧 auditor@banco.com       → Test1234!"
echo ""
