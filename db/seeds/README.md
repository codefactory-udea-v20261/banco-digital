# Scripts de Seeds — Banco Digital

Scripts de datos de prueba para entornos **LOCAL** y **TEST** únicamente.

## Orden de ejecución

```bash
psql -U app_user -d banco_digital \
  -f 01_seed_clientes.sql \
  -f 02_seed_cuentas.sql  \
  -f 03_seed_usuarios_auth.sql
```

## Datos disponibles

| Script | Contenido | Escenarios cubiertos |
|--------|-----------|----------------------|
| `01_seed_clientes.sql` | 5 clientes (4 activos, 1 inactivo) |
| `02_seed_cuentas.sql` | 6 cuentas (variados estados y saldos) |
| `03_seed_usuarios_auth.sql` | Admin + Cliente + Auditor |

## UUIDs fijos para pruebas

Los IDs son deterministas para que los tests de integración puedan usarlos sin consultar la BD:

```
Cliente María González → a1000000-0000-0000-0000-000000000001
Cuenta activa María    → b2000000-0000-0000-0000-000000000001
Cuenta saldo cero      → b2000000-0000-0000-0000-000000000003
Cuenta inactiva        → b2000000-0000-0000-0000-000000000005
```

## Contraseña de usuarios seed
Todos los usuarios usan `Test1234!` — **NUNCA usar en producción.**
