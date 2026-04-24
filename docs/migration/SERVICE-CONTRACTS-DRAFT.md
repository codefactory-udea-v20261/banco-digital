# Contratos Iniciales Entre Servicios

Este documento no pretende cerrar todos los detalles desde ya. Su objetivo es dejar una primera version de los contratos que necesitamos para empezar la extraccion sin improvisar sobre la marcha.

## 1. `identity-service`

### Responsabilidad

- Login y logout.
- Emision y revocacion de JWT.
- Consulta de identidad autenticada.
- Provisionamiento de acceso para nuevos clientes.

### Endpoints minimos propuestos

#### `POST /api/v1/auth/login`

Request:

```json
{
  "correo": "asesor@banco.com",
  "clave": "Temp1234!",
  "mfaCode": "123456"
}
```

Response:

```json
{
  "token": "jwt-token"
}
```

#### `POST /api/v1/auth/logout`

Headers:

- `Authorization: Bearer <token>`

Response:

- `204 No Content`

#### `GET /api/v1/auth/me`

Uso:

- Permite conocer la identidad autenticada sin exponer detalles internos del token.

Response:

```json
{
  "userId": "uuid",
  "username": "cliente@correo.com",
  "clienteId": "uuid",
  "roles": ["CLIENTE"]
}
```

#### `POST /api/v1/internal/users/provision-client-access`

Uso:

- Endpoint interno para aprovisionar acceso de un nuevo cliente desde el core.

Request:

```json
{
  "clienteId": "uuid",
  "email": "cliente@correo.com"
}
```

Response:

```json
{
  "userId": "uuid",
  "clienteId": "uuid",
  "email": "cliente@correo.com",
  "status": "PROVISIONED"
}
```

## 2. `core-banking-service`

### Responsabilidad

- Gestion de clientes.
- Gestion de cuentas.
- Operaciones transaccionales.

### Endpoints que deben quedarse en el core

- `POST /api/v1/clientes`
- `GET /api/v1/clientes/{id}`
- `PATCH /api/v1/clientes/{id}`
- `POST /api/v1/cuentas`
- `GET /api/v1/cuentas/{id}/saldo`

### Integraciones esperadas

- Consumir identidad autenticada sin conocer implementacion interna de JWT.
- Invocar aprovisionamiento de acceso en `identity-service`.
- Publicar eventos para futuros consumidores.

## 3. `reporting-service`

### Responsabilidad

- Consultas de lectura pesada.
- Reportes consolidados.
- Historicos y agregaciones.

### Endpoints minimos propuestos

#### `GET /api/v1/reportes/saldo-total`

Uso:

- Consolidar el saldo total de las cuentas activas del cliente autenticado.

Response:

```json
{
  "success": true,
  "data": {
    "clienteId": "uuid",
    "saldoTotal": 150000.00
  }
}
```

#### `GET /api/v1/reportes/movimientos`

Uso:

- Futuro endpoint para historial de transacciones con filtros de fecha, cuenta o tipo.

Query params sugeridos:

- `clienteId`
- `cuentaId`
- `fechaDesde`
- `fechaHasta`
- `tipo`

## 4. Integracion recomendada

### Entre `core-banking` e `identity`

En esta etapa:

- HTTP sincronico para login, logout y aprovisionamiento.
- Principal autenticado en el core sin parsear directamente JWT.

Mas adelante:

- Validacion de token en gateway o introspeccion centralizada.
- Eventos para cambios de estado de usuario si hace falta.

### Entre `core-banking` y `reporting`

En esta etapa:

- `reporting` puede seguir leyendo desde la misma base o una replica, segun el avance real del proyecto.

Mas adelante:

- Replica de lectura.
- Outbox o eventos de dominio para poblar vistas de reporte.

## 5. Decisiones pendientes

- Si `identity-service` expondra introspeccion de token o solo `me`.
- Si el aprovisionamiento de acceso sera sincronico o por evento.
- Si `reporting` trabajara con funciones SQL, vistas materializadas o proyecciones.
- Si se necesitara un API Gateway en la siguiente iteracion o todavia no.
