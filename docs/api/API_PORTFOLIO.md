# Portafolio de APIs

Actualmente el backend expone varias APIs REST separadas por dominio dentro del monolito modular. Esta organizacion nos permite documentar mejor el sistema y, al mismo tiempo, dejar listo el terreno para una migracion gradual a microservicios.

## APIs disponibles

| API | Base path | Objetivo | Endpoints principales |
|-----|-----------|----------|-----------------------|
| Autenticacion | `/api/v1/auth` | Emitir y revocar tokens JWT para acceso seguro y exponer identidad autenticada | `POST /login`, `POST /logout`, `GET /me` |
| Clientes | `/api/v1/clientes` | Registrar, consultar y actualizar perfiles de cliente | `POST /`, `GET /{id}`, `PATCH /{id}` |
| Cuentas | `/api/v1/cuentas` | Crear cuentas y consultar saldos operacionales | `POST /`, `GET /{id}/saldo` |
| Reportes | `/api/v1/reportes` | Resolver consultas analiticas y saldos consolidados | `GET /saldo-total`, `GET /movimientos`, `GET /resumen-movimientos`, `GET /cuentas` |

## Documentacion OpenAPI

Con la aplicacion levantada en local:

| Recurso | URL |
|---------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI global | `http://localhost:8080/api-docs` |
| OpenAPI Autenticacion | `http://localhost:8080/api-docs/auth` |
| OpenAPI Clientes | `http://localhost:8080/api-docs/clientes` |
| OpenAPI Cuentas | `http://localhost:8080/api-docs/cuentas` |
| OpenAPI Reportes | `http://localhost:8080/api-docs/reportes` |

Swagger UI detecta estos grupos y permite revisarlos por separado.

## Consumidores previstos

| API | Consumidores previstos |
|-----|------------------------|
| Autenticacion | Frontend web, apps moviles, personal interno |
| Clientes | Backoffice comercial, onboarding digital, portal de clientes |
| Cuentas | Portal de clientes, procesos operativos, futuros servicios transaccionales |

## Mapeo a la arquitectura objetivo

| Microservicio objetivo | APIs y dominios asociados |
|------------------------|---------------------------|
| `identity-service` | `auth` |
| `core-banking-service` | `clientes`, `cuentas`, `transactions` |
| `reporting-service` | `reportes` |

`transactions` se mantiene dentro del core porque comparte consistencia transaccional con cuentas y clientes. `reportes` se separa porque responde mejor a un servicio de lectura y analitica.

En esta etapa solo `GET /saldo-total` queda funcional. Los demas endpoints de `reporting` se dejan definidos como estructura y contrato para su implementacion posterior.
