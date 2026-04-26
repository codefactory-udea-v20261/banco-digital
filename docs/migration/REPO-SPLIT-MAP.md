# Mapa de Carpetas para Separación de Repositorios

Este documento define qué componentes del monolito actual irán a cada uno de los nuevos repositorios (`banco-digital-core-banking`, `banco-digital-identity`, `banco-digital-reporting`).

## 1. `banco-digital-core-banking`
Este será el servicio principal y conservará la mayor parte de la base actual.
- **Módulos de Negocio:**
  - `src/main/java/com/udea/bancodigital/accounts/**`
  - `src/main/java/com/udea/bancodigital/customers/**`
  - `src/main/java/com/udea/bancodigital/transactions/**`
- **Módulos Compartidos:**
  - `src/main/java/com/udea/bancodigital/shared/**` (Solo utilidades genéricas, excepciones, DTOs compartidos de error).
- **Configuración:**
  - `src/main/java/com/udea/bancodigital/infrastructure/config/**`
  - `application-core.yml`
- **Base de Datos:**
  - Scripts Flyway (`db/migration`) referentes a `cuentas`, `clientes`, `transacciones`.
- **Pruebas:**
  - `src/test/java/com/udea/bancodigital/accounts/**`
  - `src/test/java/com/udea/bancodigital/customers/**`
  - `src/test/java/com/udea/bancodigital/transactions/**`

## 2. `banco-digital-identity`
Responsable de la autenticación y emisión de JWT.
- **Módulos de Negocio:**
  - `src/main/java/com/udea/bancodigital/auth/**` (Incluye application, domain e infrastructure).
- **Configuración:**
  - Nueva clase `SecurityConfig` simplificada para este servicio.
  - `application-identity.yml`
- **Base de Datos:**
  - Scripts Flyway referentes a `usuarios`, `roles`, `usuarios_roles`, `tokens_revocados`.
- **Pruebas:**
  - `src/test/java/com/udea/bancodigital/auth/**`

## 3. `banco-digital-reporting`
Responsable de la lectura de reportes analíticos consolidados.
- **Módulos de Negocio:**
  - `src/main/java/com/udea/bancodigital/reporting/**`
- **Configuración:**
  - Nueva clase `SecurityConfig` para validar JWT (usando clave pública/secreta compartida).
  - Configuración de BD de lectura.
  - `application-reporting.yml`
- **Base de Datos:**
  - Scripts Flyway referentes a vistas y procedimientos almacenados (ej. `V6__create_summary_procedure.sql`).
- **Pruebas:**
  - `src/test/java/com/udea/bancodigital/reporting/**`

## 4. Archivos a Eliminar o Refactorizar en Todos los Repos
- `BancoDigitalApplication.java` será renombrado a `CoreBankingApplication.java`, `IdentityApplication.java`, etc.
- El archivo `docker-compose.yml` del monolito se transformará para orquestar los 3 servicios y una base de datos PostgreSQL compartida (por ahora) o múltiples bases de datos según se avance.
