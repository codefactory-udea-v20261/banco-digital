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

---

## Arranque Local (Paso a Paso)

Sigue estos pasos para configurar y correr el proyecto desde cero, incluso si no tienes la base de datos creada en tu máquina. El proyecto utiliza Flyway, por lo que las tablas e inserts iniciales se crearán automáticamente al iniciar la aplicación.

### 1. Pre-requisitos
*   **Java 21** instalado en tu sistema.
*   **Docker Desktop** (o Docker Engine + Docker Compose) instalado y ejecutándose. Esto es **obligatorio** para levantar la base de datos local fácilmente sin tener que instalar PostgreSQL manualmente en tu equipo.
*   Tu IDE favorito (**IntelliJ IDEA**, **VSCode**, etc.).

### 2. Configuración de Variables de Entorno
Abre una terminal en la raíz del proyecto y ejecuta estos comandos para crear tus archivos de configuración local a partir de los ejemplos:

**En Linux / macOS:**
```bash
cp .env.example .env
cp docker-compose.override.yml.example docker-compose.override.yml
```

**En Windows (PowerShell):**
```powershell
Copy-Item .env.example -Destination .env
Copy-Item docker-compose.override.yml.example -Destination docker-compose.override.yml
```

> **Nota:** Puedes dejar los valores por defecto que se copian en el archivo `.env`, ya están configurados para que todo funcione localmente (se creará la base de datos `banco_digital` con usuario `postgres` y contraseña `admin`).

### 3. Levantar la Base de Datos Local
En la misma terminal, ejecuta Docker Compose para levantar el motor de base de datos (asegúrate de que Docker Desktop esté abierto):

```bash
docker compose up -d db
```
*Este comando descargará la imagen de PostgreSQL y creará un contenedor llamado `banco_digital_db`. La base de datos vacía quedará lista y expuesta en el puerto `5432`.*

### 4. Ejecutar la Aplicación

Puedes iniciar el proyecto de cualquiera de las siguientes 3 formas:

#### Opción A: Desde la Consola (Recomendado)
Ejecuta el siguiente comando en la raíz del proyecto:
**Linux / macOS:**
```bash
./mvnw spring-boot:run
```
**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

#### Opción B: Desde IntelliJ IDEA
1. Abre el proyecto en IntelliJ (`File > Open...` y selecciona la carpeta `banco-digital`).
2. Espera a que IntelliJ indexe y descargue todas las dependencias de Maven.
3. En el panel del proyecto, ve a `src/main/java/com/udea/bancodigital/BancoDigitalApplication.java`.
4. Haz clic en el botón verde de "Play" (▶) que aparece a la izquierda de `public class BancoDigitalApplication` o `public static void main`.

#### Opción C: Desde Visual Studio Code (VSCode)
1. Abre la carpeta del proyecto en VSCode.
2. Asegúrate de tener instalada la extensión **"Extension Pack for Java"** de Microsoft.
3. Ve a la pestaña de "Spring Boot Dashboard" o "Java Projects" en el panel lateral, o simplemente abre el archivo `src/main/java/com/udea/bancodigital/BancoDigitalApplication.java`.
4. Haz clic en **"Run"** sobre el método `main`.

---
*Una vez que la aplicación arranque, verás en la consola que **Flyway** detecta la base de datos vacía, ejecuta automáticamente los scripts de migración (`V1__...`, `V2__...`, etc.) creando todas las tablas y finalmente el servidor quedará corriendo.*

### 5. Cargar Datos de Prueba (Seeds)

**Importante:** Este paso debes hacerlo **después** de haber ejecutado la aplicación por primera vez (Paso 4), ya que la aplicación es la encargada de crear las tablas en la base de datos a través de Flyway.

Si deseas contar con datos iniciales (clientes, cuentas, usuarios de autenticación) para poder probar la API o la colección de Postman, abre una nueva terminal en la raíz del proyecto (mientras la base de datos y la aplicación están corriendo) y ejecuta:

**En Linux / macOS:**
```bash
docker exec -i banco_digital_db psql -U postgres -d banco_digital < db/seeds/01_seed_clientes.sql
docker exec -i banco_digital_db psql -U postgres -d banco_digital < db/seeds/02_seed_cuentas.sql
docker exec -i banco_digital_db psql -U postgres -d banco_digital < db/seeds/03_seed_usuarios_auth.sql
```

**En Windows (PowerShell):**
```powershell
Get-Content db\seeds\01_seed_clientes.sql | docker exec -i banco_digital_db psql -U postgres -d banco_digital
Get-Content db\seeds\02_seed_cuentas.sql | docker exec -i banco_digital_db psql -U postgres -d banco_digital
Get-Content db\seeds\03_seed_usuarios_auth.sql | docker exec -i banco_digital_db psql -U postgres -d banco_digital
```

*(Para ver más detalles de los datos insertados, IDs estáticos y las contraseñas de prueba de los usuarios creados, puedes revisar `db/seeds/README.md`)*.

### URLs importantes (Una vez corriendo)
| Recurso       | URL                                         |
|---------------|---------------------------------------------|
| API Base      | http://localhost:8080/api/v1                |
| Swagger UI    | http://localhost:8080/swagger-ui.html       |
| OpenAPI JSON  | http://localhost:8080/api-docs              |
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

## Despliegue

Producción en **Render.com** vía Docker Compose. Configuración en [`render.yaml`](render.yaml).

CI/CD con **GitHub Actions** → build → test → SonarCloud → Docker build.
