# 🌱 Database Seeds - Banco Digital v3.0

Seed data scripts para la base de datos de Banco Digital con todos los datos de prueba pre-cargados.

## 📋 Contenido de los Seeds

### Archivo 00_drop_and_recreate.sql
**Propósito:** Limpiar datos previos (elimina sin eliminar tablas)

Limpia:
- `usuario_rol` - Asociaciones usuario-rol
- `usuario` - Usuarios de autenticación
- `rol` - Roles del sistema
- `transaccion` - Transacciones bancarias
- `cuenta` - Cuentas bancarias
- `cliente` - Clientes de negocio
- `evento_auditoria` - Eventos de auditoría

⚠️ **Advertencia:** Este script elimina TODOS los datos. Solo usar en desarrollo local.

---

### Archivo 01_seed_roles.sql
**Propósito:** Crear los 4 roles del sistema

Crea:
```
ID 1 → ADMIN       (Acceso total)
ID 2 → CAJERO      (Asesor / Cajero)
ID 3 → CLIENTE     (Cliente normal)
ID 4 → AUDITOR     (Solo lectura de auditoría)
```

**Dependencia:** Ninguna (primero)

---

### Archivo 02_seed_clientes.sql
**Propósito:** Crear 5 clientes de negocio

Crea 5 clientes con datos completos:
1. María González (a1000000-0000-0000-0000-000000000001)
2. Carlos Rodríguez (a1000000-0000-0000-0000-000000000002)
3. Valentina Martínez (a1000000-0000-0000-0000-000000000003)
4. Andrés Castro (a1000000-0000-0000-0000-000000000004)
5. Luisa Herrera (a1000000-0000-0000-0000-000000000005)

**Campos:**
- ID, Cédula, Nombres, Email, Teléfono, Fecha Nacimiento, Estado

**Dependencia:** Ninguna (ejecutar en CORE_DB)

---

### Archivo 03_seed_usuarios.sql
**Propósito:** Crear 4 usuarios de autenticación

Crea 4 usuarios con contraseñas BCrypt (factor 12):

| Email | Password | Role |
|-------|----------|------|
| cliente@banco.com | Temp1234! | CLIENTE |
| asesor@banco.com | Test1234! | CAJERO |
| admin@banco.com | Test1234! | ADMIN |
| auditor@banco.com | Test1234! | AUDITOR |

**Contraseñas BCrypt:**
- cliente@banco.com: `$2b$12$bWEN8uPKuPk7LZUpwlzE2uN8LDz61wXlFoOfIuALHOBu0H.F9Md.m`
- asesor@banco.com: `$2b$12$xloEKZpEvdOKbEbvaGFH0.s/dJyWgGXHeTCQrY.yua0e7vLiCur1e`
- admin@banco.com: `$2b$12$P3BPsTEh/87IADzcb2ElGuWpp0hC3Ryxas6xZ1hdhSaQCMah4cQzK`
- auditor@banco.com: `$2b$12$2l2.aGwPP2Tw.MWCEiYPGuIymT1UYcy2WRdtka8hKFbO6kYKINLQa`

**Dependencia:** 01_seed_roles.sql (ejecutar en IDENTITY_DB)

---

### Archivo 04_seed_usuario_roles.sql
**Propósito:** Asignar roles a usuarios

Asigna:
```
cliente@banco.com  → rol_id = 3 (CLIENTE)
asesor@banco.com   → rol_id = 2 (CAJERO)
admin@banco.com    → rol_id = 1 (ADMIN)
auditor@banco.com  → rol_id = 4 (AUDITOR)
```

**Dependencia:** 01_seed_roles.sql y 03_seed_usuarios.sql (ejecutar en IDENTITY_DB)

---

### Archivo 05_seed_cuentas.sql
**Propósito:** Crear 7 cuentas bancarias

Crea 7 cuentas con saldos variados:

| Número | Cliente | Tipo | Saldo |
|--------|---------|------|-------|
| 0001-0001-0001 | María González | Ahorros | $1,500,000.00 |
| 0001-0001-0002 | Carlos Rodríguez | Corriente | $250,000.00 |
| 0001-0001-0009 | Valentina Martínez | Ahorros | $50,000.00 |
| 0001-0002-0001 | María González | Corriente | $500,000.00 |
| 0002-0001-0001 | Andrés Castro | Corriente | $0.00 |
| 0002-0002-0001 | Carlos Rodríguez | Ahorros | $750,000.00 |
| 0003-0001-0001 | Luisa Herrera | Ahorros | $3,000,000.00 |

**Dependencia:** 02_seed_clientes.sql (ejecutar en CORE_DB)

---

## 🚀 Cómo Ejecutar los Seeds

### Opción 1: Script Automático (RECOMENDADO)

```bash
# Desde el directorio raíz del proyecto
bash banco-digital/db/seeds/SEED_ALL.sh
```

El script ejecuta automáticamente:
1. Limpieza de datos previos
2. Creación de roles
3. Creación de clientes
4. Creación de usuarios
5. Asignación de roles
6. Creación de cuentas

Con verificación final de datos.

---

### Opción 2: Ejecución Manual

```bash
cd banco-digital/db/seeds

# Limpiar datos previos
docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < 00_drop_and_recreate.sql

# Crear roles
docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < 01_seed_roles.sql

# Crear clientes
docker exec -i banco-digital-db psql -U postgres -d banco_digital_core < 02_seed_clientes.sql

# Crear usuarios
docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < 03_seed_usuarios.sql

# Asignar roles
docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < 04_seed_usuario_roles.sql

# Crear cuentas
docker exec -i banco-digital-db psql -U postgres -d banco_digital_core < 05_seed_cuentas.sql
```

---

### Opción 3: Línea de Comandos Única

```bash
bash -c '
  docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < banco-digital/db/seeds/00_drop_and_recreate.sql && \
  docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < banco-digital/db/seeds/01_seed_roles.sql && \
  docker exec -i banco-digital-db psql -U postgres -d banco_digital_core < banco-digital/db/seeds/02_seed_clientes.sql && \
  docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < banco-digital/db/seeds/03_seed_usuarios.sql && \
  docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity < banco-digital/db/seeds/04_seed_usuario_roles.sql && \
  docker exec -i banco-digital-db psql -U postgres -d banco_digital_core < banco-digital/db/seeds/05_seed_cuentas.sql
'
```

---

## ✅ Verificación

### Verificar que los datos fueron cargados

```bash
# Usuarios (identity DB)
docker exec -i banco-digital-db psql -U postgres -d banco_digital_identity \
  -c "SELECT username, activo FROM usuario;"

# Resultado esperado:
#      username      | activo 
# -------------------+--------
#  admin@banco.com   | t
#  asesor@banco.com  | t
#  auditor@banco.com | t
#  cliente@banco.com | t
```

```bash
# Clientes (core DB)
docker exec -i banco-digital-db psql -U postgres -d banco_digital_core \
  -c "SELECT primer_nombre, email FROM cliente;"

# Resultado esperado:
#  primer_nombre |           email           
# ---------------+---------------------------
#  Andrés        | andres.castro@test.com
#  Carlos        | carlos.rodriguez@test.com
#  Luisa         | luisa.herrera@test.com
#  María         | maria.gonzalez@test.com
#  Valentina     | valentina.m@test.com
```

```bash
# Cuentas (core DB)
docker exec -i banco-digital-db psql -U postgres -d banco_digital_core \
  -c "SELECT numero_cuenta, saldo FROM cuenta;"

# Resultado esperado:
#  numero_cuenta  |   saldo    
# ----------------+------------
#  0001-0001-0001 | 1500000.00
#  0001-0001-0002 |  250000.00
#  0001-0001-0009 |   50000.00
#  ...
```

---

## 🧪 Testing de Login

```bash
curl -s http://localhost:8081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "correo": "cliente@banco.com",
    "clave": "Temp1234!",
    "mfaCode": "123456"
  }' | jq '.token'
```

Si obtienes un JWT token, ¡todo funciona! ✅

---

## 📊 Estadísticas

| Entidad | Cantidad | Estado |
|---------|----------|--------|
| Roles | 4 | ✅ Cargados |
| Usuarios | 4 | ✅ Cargados |
| Clientes | 5 | ✅ Cargados |
| Cuentas | 7 | ✅ Cargadas |
| **Total** | **20** | **✅ OK** |

---

## 📍 Orden de Ejecución

```
IDENTITY DB:
  1. 00_drop_and_recreate.sql
  2. 01_seed_roles.sql
  3. 03_seed_usuarios.sql
  4. 04_seed_usuario_roles.sql

CORE DB:
  1. 00_drop_and_recreate.sql
  2. 02_seed_clientes.sql
  3. 05_seed_cuentas.sql
```

⚠️ **Importante:** Respetar el orden. Algunos scripts dependen de otros.

---

## 🔄 Recargar Seeds

Si necesitas recargar los seeds desde cero:

```bash
# Opción 1: Usar script automático
bash banco-digital/db/seeds/SEED_ALL.sh

# Opción 2: Reiniciar contenedores
docker-compose down
docker-compose up -d

# Esperar 10 segundos, luego:
bash banco-digital/db/seeds/SEED_ALL.sh
```

---

## 🔐 Seguridad

### Contraseñas

- Todas las contraseñas están hasheadas con BCrypt (factor 12)
- Solo para desarrollo local
- Cambiar en cualquier entorno no local

### Datos Sensibles

- Los datos de clientes son ficticios
- Los UIDs son predecibles para testing
- No contienen información real de personas

### MFA

- Deshabilitado en todos los usuarios para facilitar testing
- Habilitar en producción

---

## 🆘 Troubleshooting

### Error: "Base de datos no existe"
```bash
# Crear base de datos manualmente
docker exec -i banco-digital-db psql -U postgres \
  -c "CREATE DATABASE banco_digital_identity;"
```

### Error: "Tablas no existen"
Asegúrate de que se han ejecutado los migrations antes de los seeds.

### Error: "Foreign key violation"
Verificar que se ejecutan los scripts en el orden correcto.

### Error: "Duplicate key"
Los datos ya existen. Ejecutar `00_drop_and_recreate.sql` primero.

---

## 📚 Referencias

- [TESTING-CREDENTIALS.md](../../TESTING-CREDENTIALS.md) - Guía completa de credenciales
- [TESTING-GUIDE.md](../../TESTING-GUIDE.md) - Guía de testing
- [POSTMAN-TESTING.md](../../POSTMAN-TESTING.md) - Testing con Postman

---

## ✨ Estado

✅ **Todos los seeds verificados**
✅ **Contraseñas BCrypt validadas**
✅ **Datos de prueba completos**
✅ **Listo para testing**

**Última actualización:** 2026-04-26  
**Versión:** v3.0  
**Estatus:** ✅ Producción

---

_Documentación de Seeds para Banco Digital v3.0_
