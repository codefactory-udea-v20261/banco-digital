# Banco Digital — Backend API

> **Proyecto académico** — Universidad de Antioquia, CodeFactory 2026
> Materia: Arquitectura de Software | Docente: Diego Botia

Backend del núcleo tecnológico de un banco digital construido con **Clean Architecture**
sobre Spring Boot 3 y PostgreSQL.

---

## Equipo

| Nombre                  | Email                           | Rol                            |
|-------------------------|---------------------------------|--------------------------------|
| Estefanía Garcés        | estefania.garces@udea.edu.co    | Arquitecta Líder + DBA Líder   |
| Santiago Jiménez        | santiago.jimeneze@udea.edu.co   | Backend Core Developer         |
| Carlos Vanegas          | lcarlos.vanegas@udea.edu.co     | Backend Dev Transversal + Docs |
| Manuel Álvarez          | manuel.alvarez2@udea.edu.co     | Backend Dev + DevOps Lead      |

---

## Arquitectura

Clean Architecture + Monolito Modular. Ver [`docs/adr/ADR-001`](docs/adr/ADR-001-clean-architecture-and-data-strategy.md)

```text
src/main/java/com/udea/bancodigital/
├── {módulo}/
│   ├── domain/          # Entidades puras, puertos, excepciones de negocio
│   ├── application/     # Casos de uso, DTOs
│   └── infrastructure/  # Controladores REST, repositorios JPA, config
└── shared/              # ApiResponse, GlobalExceptionHandler, utils
```

**Módulos:** `customers` | `accounts` | `transactions` | `auth` | `audit`

### APIs REST actuales

El sistema expone APIs REST por dominio dentro del monolito modular. Esta separacion nos sirve para documentar mejor el backend, ordenar responsabilidades y preparar una migracion gradual sin romper el flujo actual del proyecto.

| API | Base path | OpenAPI |
|-----|-----------|---------|
| Autenticación | `/api/v1/auth` | `/api-docs/auth` |
| Clientes | `/api/v1/clientes` | `/api-docs/clientes` |
| Cuentas | `/api/v1/cuentas` | `/api-docs/cuentas` |
| Reportes | `/api/v1/reportes` | `/api-docs/reportes` |

### Arquitectura objetivo de migración

La recomendacion en este punto no es crear un microservicio por cada modulo. El corte que mejor se ajusta a lo que ya existe es este:

| Microservicio | Responsabilidad |
|---------------|-----------------|
| `core-banking-service` | Clientes, cuentas y transacciones |
| `identity-service` | Autenticación, autorización, JWT, MFA |
| `reporting-service` | Consultas analíticas, saldos consolidados, reportes |

Ver también [`docs/api/API_PORTFOLIO.md`](docs/api/API_PORTFOLIO.md), [`docs/adr/ADR-002`](docs/adr/ADR-002-api-portfolio-and-microservices-roadmap.md) y [`docs/adr/ADR-003`](docs/adr/ADR-003-target-microservices-architecture.md).

---

## Configuración del Entorno Local

Esta sección detalla el procedimiento para configurar y ejecutar el entorno de desarrollo local. El proyecto utiliza Docker para la virtualización de la base de datos y Flyway para el control de versiones del esquema de datos.

### 💡 Arquitectura de Datos Local
El flujo de inicialización de la base de datos es **completamente automatizado** mediante la interacción de tres componentes clave:
1. **Docker:** Descarga y ejecuta el motor de PostgreSQL aislado, creando la base de datos `banco_digital` en su interior.
2. **Flyway (Spring Boot):** Durante el arranque de la aplicación, intercepta la conexión a la base de datos y ejecuta los scripts de migración (`src/main/resources/db/migration`), materializando el esquema relacional (tablas, restricciones y roles).
3. **Seed Scripts:** Proveen un estado base predecible con datos de prueba (clientes, cuentas, etc.) para facilitar la validación inmediata de la API.

---

### Paso 1: Requisitos del Sistema
*   **Java Development Kit (JDK) 21**
*   **Docker Desktop** (o Docker Engine + Compose). *Es indispensable para garantizar la paridad del entorno de base de datos entre el equipo de desarrollo.*
*   Entorno de Desarrollo Integrado (IDE) compatible (IntelliJ IDEA, VSCode, Eclipse).

### Paso 2: Resolución de Conflictos de Puertos (Importante)
Para evitar colisiones de red (Error: `FATAL: no existe la base de datos "banco_digital"` o "Port already in use"), es imperativo garantizar que el puerto **5432** se encuentre disponible para Docker.

Si cuenta con una instalación nativa de PostgreSQL en su sistema operativo:
*   **Windows:** Presione `Win + R`, escriba `services.msc`, localice el servicio de PostgreSQL (ej. `postgresql-x64-15`), haga clic derecho y seleccione **Detener**.
*   **Linux (Systemd):** Ejecute `sudo systemctl stop postgresql`.
*   **macOS (Homebrew):** Ejecute `brew services stop postgresql`.

### Paso 3: Variables de Entorno
Abra una terminal en la raíz del repositorio y genere los archivos de configuración local ejecutando:

**Linux / macOS:**
```bash
cp .env.example .env
cp docker-compose.override.yml.example docker-compose.override.yml
```

**Windows (PowerShell):**
```powershell
Copy-Item .env.example -Destination .env
Copy-Item docker-compose.override.yml.example -Destination docker-compose.override.yml
```
*(Los valores por defecto establecerán las credenciales `postgres` / `admin`).*

### Paso 4: Inicialización del Motor de Base de Datos
Construya y levante el contenedor de PostgreSQL ejecutando:

```bash
docker compose down -v  # Garantiza la eliminación de volúmenes residuales con configuraciones previas
docker compose up -d db
```
*El sistema confirmará el inicio del contenedor `banco_digital_db`.*

### Paso 5: Ejecución de la Aplicación y Migraciones
Inicie el ciclo de vida de la aplicación Spring Boot. Durante este proceso, **Flyway creará automáticamente todas las tablas requeridas**.

*   **Vía Terminal (Recomendado):**
    *   Linux / macOS: `./mvnw spring-boot:run`
    *   Windows: `mvnw.cmd spring-boot:run`
*   **Vía IDE (IntelliJ / VSCode):**
    *   Ejecute la clase principal `src/main/java/com/udea/bancodigital/BancoDigitalApplication.java`.

### Paso 6: Población de Datos de Prueba (Seeds)
**Requisito previo:** La aplicación (Paso 5) debe haberse inicializado al menos una vez para que Flyway haya construido el esquema.

Abra una nueva terminal y ejecute los siguientes comandos para inyectar los datos en el contenedor:

**Linux / macOS:**
```bash
docker exec -i banco_digital_db psql -U postgres -d banco_digital < db/seeds/01_seed_clientes.sql
docker exec -i banco_digital_db psql -U postgres -d banco_digital < db/seeds/02_seed_cuentas.sql
docker exec -i banco_digital_db psql -U postgres -d banco_digital < db/seeds/03_seed_usuarios_auth.sql
```

**Windows (PowerShell):**
```powershell
Get-Content db\seeds\01_seed_clientes.sql | docker exec -i banco_digital_db psql -U postgres -d banco_digital
Get-Content db\seeds\02_seed_cuentas.sql | docker exec -i banco_digital_db psql -U postgres -d banco_digital
Get-Content db\seeds\03_seed_usuarios_auth.sql | docker exec -i banco_digital_db psql -U postgres -d banco_digital
```
*(Consulte `db/seeds/README.md` para conocer los UUIDs y contraseñas de los usuarios generados).*

### Enlaces de Interés (Post-Arranque)
| Recurso       | URL                                         |
|---------------|---------------------------------------------|
| API Base      | http://localhost:8080/api/v1                |
| Swagger UI    | http://localhost:8080/swagger-ui.html       |
| OpenAPI JSON  | http://localhost:8080/api-docs              |
| OpenAPI Auth  | http://localhost:8080/api-docs/auth         |
| OpenAPI Clientes | http://localhost:8080/api-docs/clientes  |
| OpenAPI Cuentas | http://localhost:8080/api-docs/cuentas    |
| OpenAPI Reportes | http://localhost:8080/api-docs/reportes  |
| Health Check  | http://localhost:8080/actuator/health       |
| Métricas      | http://localhost:8080/actuator/prometheus   |

---

## Tests

**En Linux / macOS:**
```bash
./mvnw test                          # Unit tests
./mvnw verify                        # Unit + Integration tests + JaCoCo
./mvnw verify sonar:sonar            # Análisis SonarCloud
```

**En Windows:**
```cmd
mvnw.cmd test
mvnw.cmd verify
mvnw.cmd verify sonar:sonar
```

---

## Documentación

| Documento                                            | Descripción                          |
|------------------------------------------------------|--------------------------------------|
| [ADR-001](docs/adr/ADR-001-clean-architecture-and-data-strategy.md) | Decisión arquitectónica principal |
| [ADR-002](docs/adr/ADR-002-api-portfolio-and-microservices-roadmap.md) | Ruta para formalizar APIs y migrar gradualmente |
| [ADR-003](docs/adr/ADR-003-target-microservices-architecture.md) | Arquitectura objetivo de 3 microservicios |
| [Portafolio de APIs](docs/api/API_PORTFOLIO.md)     | Catálogo de APIs y su mapeo al target distribuido |
| [Plan de 1 semana](docs/migration/WEEK-PLAN-3-MICROSERVICES.md) | Plan de trabajo para la migración inicial |
| [Contratos entre servicios](docs/migration/SERVICE-CONTRACTS-DRAFT.md) | Borrador de contratos para core, identity y reporting |
| [Coding Standards](docs/CODING_STANDARDS.md)        | Convenciones del equipo              |
| [Definition of Done](docs/DEFINITION_OF_DONE.md)   | Criterios de completitud             |
| [Definition of Ready](docs/DEFINITION_OF_READY.md) | Criterios de entrada al sprint       |

---

## Estrategia de Ramas

```text
main ← develop ← feature/hu-XX-descripcion
                ← fix/descripcion
                ← chore/descripcion
```

Ver [`docs/CODING_STANDARDS.md`](docs/CODING_STANDARDS.md) para convenciones completas.

---

## Posibles Errores (Troubleshooting)

### 1. `FATAL: password authentication failed for user`
*   **Causa:** Tienes un volumen residual de Docker con credenciales antiguas.
*   **Solución:** Borra el contenedor y el volumen con `docker compose down -v` y vuelve a levantarlo con `docker compose up -d db`.

### 2. `FATAL: no existe la base de datos "banco_digital"` o "Port already in use"
*   **Causa:** Tienes una instalación de PostgreSQL local en tu computadora corriendo en segundo plano que está "secuestrando" el puerto `5432`. Spring Boot se está conectando a tu PostgreSQL local (donde no existe la BD) en lugar de conectarse al contenedor de Docker.
*   **Solución (Opción A - Recomendada):** Apaga el servicio de PostgreSQL de tu máquina (En Windows: `Servicios` > `postgresql` > Detener). Luego ejecuta `docker compose down -v` y `docker compose up -d db`.
*   **Solución (Opción B - Usar puerto alternativo):** Cambia el puerto en tu archivo `.env` a `DB_PORT=5433` y asegúrate de actualizarlo en el `docker-compose.override.yml` si lo estás usando.
*   **Solución (Opción C - Usar tu BD local sin Docker):** Abre pgAdmin o psql en tu máquina y ejecuta manualmente: `CREATE DATABASE banco_digital;`. Luego corre la aplicación normalmente.

---

## Despliegue

Producción en **Render.com** vía Docker Compose. Configuración en [`render.yaml`](render.yaml).

CI/CD con **GitHub Actions** → build → test → SonarCloud → Docker build.
