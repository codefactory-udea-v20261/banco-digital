# Roles y Permisos

## Audit DB

Se han creado los siguientes roles y permisos:
- audit_readonly: SELECT sobre audit_event
- audit_writer: INSERT sobre audit_event.
- audit_admin: ALL PRIVILEGES sobre el esquema public en la BD banco_digital_audit

## Script para PSQL

```sql
CREATE ROLE audit_readonly;
CREATE ROLE audit_writer;
CREATE ROLE audit_admin;

GRANT CONNECT ON DATABASE banco_digital_audit TO audit_reader_user;
GRANT USAGE ON SCHEMA public TO audit_readonly;
GRANT SELECT ON audit_event TO audit_readonly;

GRANT INSERT ON audit_event TO audit_writer;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO audit_admin;
```

El microservicio banco-digital-audit debería usar el rol audit_writer para hacer las respectivas inserciones a la BD y SELECT según necesidad, otros microservicios para acceder a la base de datos deben tener siquiera el audit_readonly.

## Core Banking DB

Se han creado los siguientes roles y permisos:
- core_readonly: SELECT sobre audit_event
- core_user: INSERT on audit_event (si el servicio escribe)
- audit_admin: ALL PRIVILEGES sobre el esquema public en la BD banco_digital_audit

## Script para PSQL

```sql
CREATE ROLE core_readonly: SELECT para las transacciones, clientes, etc.;
CREATE ROLE core_user: para hacer INSERT y UPDATE sobre información del cliente, cuentas, tipos de transacciones y tipos de cuentas;
CREATE ROLE core_admin: tiene todos los privilegios de la base de datos para manipular el esquema;

GRANT CONNECT ON DATABASE banco_digital_core TO core_reader_user;
GRANT USAGE ON SCHEMA public TO core_readonly;
GRANT SELECT ON transaccion TO core_readonly;

GRANT INSERT, UPDATE ON cliente, cuentas, tipo_transaccion, tipo_cuenta TO core_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO core_admin;
```

## Identity DB

Se han creado los siguientes roles y permisos:
- identity_readonly: SELECT sobre usuario, rol, permiso.
- identity_user: operaciones de usuario necesarias (INSERT usuario, UPDATE password, etc).
- identity_admin: ALL PRIVILEGES sobre el esquema public en la BD banco_digital_identity.

## Script para PSQL

```sql
CREATE ROLE identity_readonly;
CREATE ROLE identity_user;
CREATE ROLE identity_admin;

GRANT CONNECT ON DATABASE banco_digital_identity TO identity_reader_user;
GRANT USAGE ON SCHEMA public TO identity_readonly;
                      
GRANT SELECT ON usuario, rol, permiso TO identity_readonly;
GRANT SELECT, INSERT, UPDATE ON usuario TO identity_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO identity_admin;
```