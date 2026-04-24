# Plan de Trabajo de la Semana 4: Extracción a Repositorios Independientes

Este plan define los pasos exactos y el backlog técnico para ejecutar la separación física del monolito actual (`feature/microservices-foundation`) hacia los tres repositorios objetivo:

- `banco-digital-core-banking`
- `banco-digital-identity`
- `banco-digital-reporting`

El monolito actual está estable, los contratos están definidos en OpenAPI y las responsabilidades delimitadas por puertos y adaptadores. Esta semana se enfoca en la **ejecución de la separación física** sin alterar lógica de negocio.

## Objetivo de la semana

Finalizar la semana con tres repositorios independientes, cada uno construyéndose y pasando sus propias pruebas unitarias, listos para ser desplegados en entornos de desarrollo/pruebas.

## Alcance de esta semana

### Sí entra en la semana

- Creación de los tres nuevos repositorios.
- Migración del código fuente según el [Mapa de Carpetas](REPO-SPLIT-MAP.md).
- Limpieza de dependencias en los `pom.xml` de cada servicio.
- Configuración básica de CI (GitHub Actions) en cada repositorio.
- Configuración de `docker-compose` en cada servicio para desarrollo local.
- Adaptación del `CrearClienteUseCase` para usar HTTP/Eventos (Mock/REST) hacia Identity.

### No entra en la semana

- Despliegue en producción.
- Implementación de un API Gateway real (Kong, Krax, Spring Cloud Gateway).
- Implementación de Event Sourcing completo (Kafka/RabbitMQ) para todo el sistema; la comunicación inicial será REST (Sincrónica) para simplificar la extracción.

## Backlog Técnico Detallado

### Epica 1: Extracción de Identity Service

**Objetivo:** Tener `banco-digital-identity` funcionando como fuente única de verdad para la autenticación.

- [ ] **Tarea 1.1: Inicialización del Repositorio:** Crear `banco-digital-identity` siguiendo el [Checklist](REPO-CREATION-CHECKLIST.md).
- [ ] **Tarea 1.2: Migración de Código:** Copiar el paquete `com.udea.bancodigital.auth`. Copiar los paquetes genéricos compartidos (`shared.exception`, `shared.web`).
- [ ] **Tarea 1.3: Limpieza de Dependencias:** Remover dependencias innecesarias del `pom.xml` (ej. no necesitamos librerías de reportes complejos si no se usan).
- [ ] **Tarea 1.4: Configuración de Base de Datos:** Extraer solo los scripts Flyway relacionados con usuarios, roles y tokens. Configurar `application.yml` para apuntar a un esquema `identity_db`.
- [ ] **Tarea 1.5: Verificación y CI:** Asegurar que `./mvnw clean verify` pasa. Configurar GitHub Actions.

### Epica 2: Extracción de Reporting Service

**Objetivo:** Tener `banco-digital-reporting` funcionando de manera aislada para consultas de lectura.

- [ ] **Tarea 2.1: Inicialización del Repositorio:** Crear `banco-digital-reporting` siguiendo el [Checklist](REPO-CREATION-CHECKLIST.md).
- [ ] **Tarea 2.2: Migración de Código:** Copiar el paquete `com.udea.bancodigital.reporting`. Copiar paquetes compartidos.
- [ ] **Tarea 2.3: Autenticación Stateless:** Implementar un `SecurityConfig` simplificado que solo valide el JWT (verificando la firma con la clave pública o secreto compartido) sin necesidad de consultar la base de datos de Identity.
- [ ] **Tarea 2.4: Configuración de Base de Datos:** Configurar conexión a la base de datos de lectura (inicialmente puede ser la misma BD del core, pero accediendo solo a vistas).
- [ ] **Tarea 2.5: Verificación y CI:** Asegurar que `./mvnw clean verify` pasa. Configurar GitHub Actions.

### Epica 3: Consolidación del Core Banking Service

**Objetivo:** Limpiar el monolito actual convirtiéndolo en el `banco-digital-core-banking`, eliminando lo que ya se movió.

- [ ] **Tarea 3.1: Renombrado del Repositorio:** (Opcional) Renombrar el repositorio actual o crear uno nuevo a partir de este. Renombrar `BancoDigitalApplication` a `CoreBankingApplication`.
- [ ] **Tarea 3.2: Eliminación de Código Muerto:** Eliminar los paquetes `com.udea.bancodigital.auth` y `com.udea.bancodigital.reporting`.
- [ ] **Tarea 3.3: Adaptación del Aprovisionamiento (Punto Crítico):** Modificar `ClienteAccessProvisioningAdapter` en el módulo `customers`. En lugar de llamar al caso de uso local de Auth, debe realizar una petición HTTP POST al endpoint `/api/v1/internal/users/provision-client-access` del `identity-service`. Usar `RestTemplate` o `WebClient`.
- [ ] **Tarea 3.4: Autenticación Stateless:** Al igual que en Reporting, implementar la validación de JWT localmente sin depender del módulo `auth` interno (que ya no existirá).
- [ ] **Tarea 3.5: Limpieza de Base de Datos:** Eliminar de Flyway las migraciones de `auth` (se asume que la BD se recreará o se gestionará la migración de datos por separado).
- [ ] **Tarea 3.6: Verificación Final:** Ejecutar pruebas para asegurar que el core funciona sin los otros módulos.

### Epica 4: Orquestación Local (Opcional pero Recomendado)

- [ ] **Tarea 4.1: Repositorio de Orquestación:** Crear un pequeño repositorio (ej. `banco-digital-dev-env`) que contenga un `docker-compose.yml` maestro para levantar las bases de datos y (opcionalmente) los tres servicios juntos para pruebas end-to-end locales.

## Riesgos y Mitigaciones

- **Riesgo:** Comunicación entre Core e Identity falla.
  - *Mitigación:* Implementar logs detallados y manejo de errores (circuit breaker básico o reintentos) en la llamada REST del adaptador de aprovisionamiento en el Core.
- **Riesgo:** Inconsistencia de configuraciones (ej. secretos de JWT).
  - *Mitigación:* Asegurar que el secreto JWT (`app.security.jwt.secret`) sea el mismo en `application-local.yml` para los tres servicios durante el desarrollo local.

## Criterios de Éxito de la Semana 4
- Tres repositorios en GitHub.
- Los tres repositorios compilan y pasan pruebas (`mvn clean verify`).
- El Core puede aprovisionar un cliente exitosamente llamando al Identity Service mediante HTTP.
- Reporting puede validar un JWT emitido por Identity.
