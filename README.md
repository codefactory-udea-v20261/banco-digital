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

```
src/main/java/com/udea/bancodigital/
├── {módulo}/
│   ├── domain/          # Entidades puras, puertos, excepciones de negocio
│   ├── application/     # Casos de uso, DTOs
│   └── infrastructure/  # Controladores REST, repositorios JPA, config
└── shared/              # ApiResponse, GlobalExceptionHandler, utils
```

**Módulos:** `customers` | `accounts` | `transactions` | `auth` | `audit`

---

## Arranque Local

### Pre-requisitos
- JDK 21, Maven 3.9+, Docker Desktop

### Con Docker Compose (recomendado)
```bash
cp .env.example .env          # Configura tus variables
cp docker-compose.override.yml.example docker-compose.override.yml
docker compose up -d db       # Levanta solo PostgreSQL
./mvnw spring-boot:run        # Corre la app en modo local
```

### Solo app (PostgreSQL externo)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### URLs importantes
| Recurso       | URL                                         |
|---------------|---------------------------------------------|
| API Base      | http://localhost:8080/api/v1                |
| Swagger UI    | http://localhost:8080/swagger-ui.html       |
| OpenAPI JSON  | http://localhost:8080/api-docs              |
| Health Check  | http://localhost:8080/actuator/health       |
| Métricas      | http://localhost:8080/actuator/prometheus   |

---

## Tests

```bash
./mvnw test                          # Unit tests
./mvnw verify                        # Unit + Integration tests + JaCoCo
./mvnw verify sonar:sonar            # Análisis SonarCloud
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

```
main ← develop ← feature/hu-XX-descripcion
                ← fix/descripcion
                ← chore/descripcion
```

Ver [`docs/CODING_STANDARDS.md`](docs/CODING_STANDARDS.md) para convenciones completas.

---

## Despliegue

Producción en **Render.com** vía Docker Compose. Configuración en [`render.yaml`](render.yaml).

CI/CD con **GitHub Actions** → build → test → SonarCloud → Docker build.
