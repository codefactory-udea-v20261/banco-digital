all# Roles y Permisos - Audit

Se han creado los siguientes roles y permisos:
- audit_readonly: SELECT sobre audit_event
- audit_writer: INSERT on audit_event (si el servicio escribe)
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