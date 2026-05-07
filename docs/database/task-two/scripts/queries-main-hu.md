# Queries basadas en Historias de Usuario (HU)

Este documento describe las consultas SQL diseñadas para soportar las diferentes historias de usuario (HU) de un sistema bancario. Cada sección explica el propósito de las queries, las validaciones necesarias y la lógica aplicada.

Se usan datos que son aceptados por la base de datos mas no existen en realidad, además pueden tener una longitud variable.

---

## HU-01: Registro de clientes

Antes de registrar un nuevo cliente, es necesario verificar que no exista previamente en la base de datos para evitar duplicados.

### Validación: existencia de cliente

```sql
SELECT EXISTS (
    SELECT 1 
    FROM cliente 
    WHERE numero_cedula = '123'
) AS existe;
```

- Retorna `true` si el cliente ya existe.
- Retorna `false` si es un cliente nuevo.
- La creación del ID del cliente es responsabilidad del backend.

---

## HU-02: Consulta de información de cliente

Permite obtener la información de un cliente a partir de su número de cédula.

```sql
SELECT 
    id,
    numero_cedula,
    primer_nombre,
    segundo_nombre,
    primer_apellido,
    segundo_apellido,
    email,
    telefono,
    fecha_nacimiento,
    activo,
    created_at
FROM cliente
WHERE numero_cedula = '123';
```

- Consulta directa sin lógica compleja.
- Se basa en un identificador único (cédula).

---

## HU-03: Actualización de información de cliente

Antes de actualizar, se debe validar que el cliente exista.

```sql
SELECT EXISTS (
    SELECT 1 
    FROM cliente 
    WHERE id = 'a1000000-0200-0000-0000-000030000001'
) AS existe;
```

- Si no existe, no se debe permitir la actualización.

---

## HU-04: Crear cuenta financiera

Requiere varias validaciones.

### 1. Validar que el cliente exista y esté activo

```sql
SELECT EXISTS (
    SELECT 1 
    FROM cliente 
    WHERE id = '1234' AND activo = true
) AS cliente_valido;
```

### 2. Validar que la cuenta no exista

```sql
SELECT EXISTS (
    SELECT 1 
    FROM cuenta 
    WHERE numero_cuenta = '123'
) AS existe;
```

---

## HU-06: Consultar saldo

Se debe validar:

- Que la cuenta pertenezca al usuario.
- Que la cuenta esté activa.

```sql
SELECT cu.saldo
FROM cuenta cu
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE cu.numero_cuenta = '123'
AND u.id = 'c3000000-1000-2000-3000-400000000003'
AND cu.estado = 'ACTIVA';
```

---

## HU-07: Transferir dinero

Se maneja mediante transacciones para garantizar consistencia.

```sql
BEGIN;
```

### 1. Validar cuenta origen

```sql
SELECT cu.id, cl.saldo
FROM cuenta cu
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE cu.numero_cuenta = '123'
AND u.id = 'c3000020-3000-4000-5000-000000000001'
AND cu.estado = 'ACTIVA'
FOR UPDATE;
```

### 2. Validar cuenta destino

```sql
SELECT id
FROM cuenta
WHERE numero_cuenta = '456'
AND estado = 'ACTIVA'
FOR UPDATE;
```

- `FOR UPDATE` bloquea los registros para evitar inconsistencias concurrentes.
- Luego se ejecuta la lógica de débito/crédito en backend.

```sql
COMMIT;
```

---

## HU-08: Retiro de dinero

Similar a la transferencia, pero más simple.

```sql
BEGIN;
```

### Validación de cuenta

```sql
SELECT cu.id, cl.saldo
FROM cuenta cu
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE cu.numero_cuenta = '123'
AND u.id = 'c3000020-3000-4000-5000-000000000001'
AND cu.estado = 'ACTIVA'
FOR UPDATE;
```

- Luego se aplica la lógica de retiro.

```sql
COMMIT;
```

---

## HU-09: Consultar historial de operaciones

Permite ver el historial de transacciones asociadas a una cuenta.

```sql
SELECT 
    tr.created_at,
    tr.tipo_id,
    tr.monto,
    tr.descripcion,
    tr.referencia,
    tr.estado
FROM transaccion tr
JOIN cuenta cu 
    ON cu.id = 'c3000020-3000-4000-5000-000000000001'
JOIN usuario u 
    ON u.cliente_id = cu.cliente_id
WHERE u.id = 'a3000020-3000-4000-5000-000000000001'
AND (
    tr.cuenta_origen_id = cu.id 
    OR tr.cuenta_destino_id = cu.id
)
ORDER BY tr.created_at DESC;
```

---

## HU-10: Reporte de actividad de mis cuentas

Genera un resumen de transacciones en un período determinado.

```sql
SELECT 
    COUNT(*) AS total_transacciones,

    SUM(CASE WHEN tr.tipo_id = 1 THEN tr.monto ELSE 0 END) AS total_depositos,
    SUM(CASE WHEN tr.tipo_id = 2 THEN tr.monto ELSE 0 END) AS total_retiros,
    SUM(CASE WHEN tr.tipo_id = 3 THEN tr.monto ELSE 0 END) AS total_transferencias,
    SUM(CASE WHEN tr.tipo_id = 4 THEN tr.monto ELSE 0 END) AS total_pagos,

    MAX(tr.saldo_posterior) AS saldo_final_estimado

FROM transaccion tr
JOIN cuenta cu ON cu.id = 'a3000020-3000-4000-5000-000000000001'
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE u.id = 'c3000020-3000-4000-5000-000000000001'
AND (tr.cuenta_origen_id = cu.id OR tr.cuenta_destino_id = cu.id)
AND tr.created_at BETWEEN '2026-04-01 00:00:00' 
                     AND '2026-04-30 23:59:59';
```

- Permite obtener métricas agregadas.
- Útil para reportes financieros.

---

## HU-11: Login y Logout

### 1. Validar credenciales

```sql
SELECT 
    u.id,
    u.username,
    u.password_hash,
    u.activo,
    u.intentos_fallidos,
    u.bloqueado_hasta,
    u.cliente_id
FROM usuario u
WHERE u.username = 'ejemplo@test.com';
```

### 2. Verificar bloqueo de usuario

```sql
SELECT bloqueado_hasta
FROM usuario
WHERE username = 'asesor@banco.com';
```

### 3. Obtener datos del cliente asociado

```sql
SELECT 
    cl.id,
    cl.primer_nombre,
    cl.primer_apellido,
    cl.numero_cedula
FROM cliente cl
JOIN usuario u ON u.cliente_id = cl.id
WHERE u.username = 'ejemplo@test.com';
```

### 4. Validar existencia de usuario

```sql
SELECT EXISTS (
    SELECT 1 
    FROM usuario 
    WHERE username = 'ejemplo@test.com'
);
```

---

Gran parte de la lógica de negocio (como validaciones adicionales, cálculos y reglas) debe implementarse en el backend, usando estas consultas como base de acceso a los datos.

---