# ADR-003: Arquitectura Objetivo de 3 Microservicios

| Campo | Valor |
|-------|-------|
| **Estado** | Propuesto |
| **Fecha** | 2026-04-09 |
| **Autores** | Equipo Banco Digital |
| **Contexto** | Evolucion del monolito modular a una arquitectura distribuida con menor acoplamiento |

---

## 1 Contexto

El sistema actual ya tiene limites modulares claros, pero eso no significa que todos los modulos deban convertirse en microservicios desde ya. La prioridad en esta etapa es definir un corte que tenga sentido desde el negocio, mantenga una alta cohesion y no comprometa la consistencia de las operaciones bancarias.

## 2 Decision

La arquitectura objetivo propuesta tendra tres microservicios:

1. `core-banking-service`
2. `identity-service`
3. `reporting-service`

## 3 Responsabilidades

### 3.1 `core-banking-service`

Incluye:

- `customers`
- `accounts`
- `transactions`

Responsabilidad principal:

- Gestionar clientes, cuentas y operaciones financieras de escritura.
- Mantener consistencia transaccional del negocio.
- Publicar eventos de dominio para integraciones posteriores.

### 3.2 `identity-service`

Incluye:

- Autenticacion
- Autorizacion
- Usuarios
- Roles
- JWT
- MFA

Responsabilidad principal:

- Validar identidad.
- Emitir y revocar tokens.
- Centralizar politicas de acceso.

### 3.3 `reporting-service`

Incluye:

- Reportes de saldo consolidado
- Historiales
- Consultas analiticas
- Futuras vistas materializadas o replicas de lectura

Responsabilidad principal:

- Resolver consultas de lectura pesada.
- Aislar cargas analiticas del flujo OLTP.

## 4 Justificacion del corte

### 4.1 Cohesion alta

- `customers`, `accounts` y `transactions` forman un mismo nucleo de negocio. Separarlos demasiado temprano obligaria a resolver consistencia distribuida en operaciones financieras que hoy conviene mantener bajo una sola transaccion.
- `identity` tiene reglas propias, tablas propias y preocupaciones de seguridad que no pertenecen al core bancario.
- `reporting` responde a un patron diferente: mas lectura, mas agregacion y menos operaciones criticas de escritura.

### 4.2 Bajo acoplamiento

- El core no debe conocer detalles internos de JWT ni de persistencia de usuarios.
- Los reportes no deben depender de la misma transaccion ACID del request principal.
- Las integraciones entre servicios se basaran en contratos y eventos, no en clases compartidas.

## 5 Principios de diseno aplicados

### SOLID

- **SRP:** cada servicio tiene una razon clara de cambio.
- **OCP:** la evolucion de reportes no debe modificar el core bancario.
- **LSP:** los puertos internos permiten reemplazar adaptadores sin cambiar casos de uso.
- **ISP:** los contratos entre servicios deben ser pequenos y orientados a caso de uso.
- **DIP:** el dominio depende de puertos; la infraestructura implementa los detalles.

### GRASP

- **High Cohesion:** el core conserva la logica transaccional que pertenece al mismo proceso de negocio.
- **Low Coupling:** `identity` y `reporting` se separan por responsabilidad, no por entidad.
- **Indirection:** la comunicacion se realiza mediante puertos, adaptadores y eventos.
- **Protected Variations:** el core queda protegido frente a cambios en JWT, MFA o mecanismos de reporte.
- **Controller:** cada API expone un punto de entrada claro por contexto.

## 6 Reglas de integracion

1. `identity-service` autentica y entrega identidad confiable; no ejecuta logica bancaria.
2. `core-banking-service` no parsea JWT ni depende de clases internas de seguridad.
3. `reporting-service` consume datos por replicas, eventos, vistas o consultas dedicadas; no participa en escrituras criticas.
4. Ningun servicio comparte entidades JPA ni tablas como contrato publico.

## 7 Orden de migracion

1. Extraer primero la logica de identidad.
2. Mantener `core-banking` como un solo servicio transaccional.
3. Separar `reporting` como servicio de lectura.
4. Evaluar futuros servicios como `audit` o `notification` solo cuando exista una necesidad operacional real.

## 8 Consecuencias

### Positivas

- Menor riesgo al migrar.
- Mejor alineacion con principios arquitectonicos.
- Escalamiento independiente para seguridad y analitica.

### Negativas

- El core seguira siendo el servicio mas grande en la primera etapa.
- Se requiere disciplina en contratos y ownership de datos.
