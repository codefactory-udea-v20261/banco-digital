### TABLA: cliente 
Escenario: Estimando un banco para una población colombiana con aproximadamente 48.258.494 Millones de habitantes y calculando que el 80% tendrá un producto financiero, se realiza un cálculo para 38.606.795 Millones de habitantes, esta sería la cantidad estimada en bytes de los posibles registros en la tabla clientes

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | numero_cedula | 8 | 16 | 2 | 11 |
| fecha_nacimiento | primer_nombre |  | 4 |  | 16 |
| activo | segundo_nombre |  | 1 |  | 11 |
| created_at | primer_apellido |  | 8 |  | 16 |
| updated_at | segundo_apellido |  | 8 |  | 11 |
|  | email |  |  |  | 31 |
|  | teléfono |  |  |  | 11 |
|  | created_by |  |  |  | 21 |
|  |  |  |  |  |  |
| LongitudPorRegistro | 199 | Bytes |  |  |  |
| TotalBytesEnTabla | 7.682.752.205 | Bytes |  |  |  |

### TABLA: cuenta
Para una población colombiana de aproximadamente 48.258.494 habitantes, se estima que el 80% tendrá una cuenta bancaria, lo que equivale a 38.606.795 personas; esta cifra se utiliza para calcular el tamaño en bytes de los posibles registros en la tabla cuenta.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | numero_cuenta | 5 | 16 | 2 | 13 |
| cliente_id | saldo |  | 16 |  | 16 |
| tipo_cuenta_id | estado |  | 2 |  | 11 |
| fecha_apertura | created_by |  | 4 |  | 21 |
| created_at | updated_by |  | 8 |  | 21 |
| updated_at |  |  | 8 |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 158 | Bytes |  |  |  |
| TotalBytesEnTabla | 6.099.873.610 | Bytes |  |  |  |

### TABLA: tipo_cuenta
Para este cálculo se parte de dos posibles resultados en el registro, cuenta de ahorros o corriente, con lo que serían dos posibles registros por el momento; esta cifra se utiliza para calcular el tamaño en bytes de los posibles registros en la tabla tipo_cuenta.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | nombre | 2 | 2 | 1 | 15 |
|  | descripcion |  |  |  | 51 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 77 | Bytes |  |  |  |
| TotalBytesEnTabla | 154 | Bytes |  |  |  |

### TABLA: tipo_transaccion
Para esta tabla inicialmente se tienen contemplados cuatro posibles tipos de transacción, los cuales son RETIRO, DEPOSITO, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECIBIDA; con estos datos se calcular el tamaño en bytes de los posibles registros en la tabla tipo_transaccion.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | nombre | 1 | 16 | 1 | 20 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 41 | Bytes |  |  |  |
| TotalBytesEnTabla | 164 | Bytes |  |  |  |

### TABLA: auditoria
Para esta tabla se asumirá el porcentaje de auditoría a partir de la tabla transacción ya que es una de las más críticas y de las cuales consideramos puede tener mayor número de registros, por lo que asumiendo que del total de cuentas equivalente a 38.606.795  y suponiendo que  diariamente se realicen transacciones en el 10% de las cuentas daría 3.860.679 transacciones y un total de 115.820.385 transacciones mensuales, asumiendo que se audite el 10% de las transacciones mensuales nos da 11.582.038 transacciones auditadas , o también puede darse el caso en el que se audite diferentes tablas, sin embargo este porcentaje es muy acorde a lo que se podría llegar a auditar de forma general en la BD; esta última cifra se utiliza para calcular el tamaño en bytes de los posibles registros en la tabla auditoria que se generan mensualmente.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | tabla | 7 | 8 | 2 | 21 |
| created_at | operacion |  | 8 |  | 11 |
|  | registro_id |  |  |  | 37 |
|  | usuario_bd |  |  |  | 21 |
|  | ip_origen |  |  |  | 16 |
|  | datos_antes |  |  |  | 100 |
|  | datos_despues |  |  |  | 100 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 352 | Bytes |  |  |  |
| TotalBytesEnTabla | 4.076.877.376 | Bytes |  |  |  |

### TABLA: transaccion
Asumiendo que del total de cuentas equivalente a 38.606.795 y suponiendo que  diariamente se realicen transacciones en el 10% de las cuentas daría 3.860.679 transacciones y un total de 115.820.385 transacciones mensuales, con estos datos se calcular el tamaño en bytes de los posibles registros en la tabla transaccion que se generan mensualmente.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | monto | 7 | 16 | 2 | 16 |
| cuenta_origen_id | saldo_anterior |  | 16 |  | 16 |
| cuenta_destino_id | saldo_posterior |  | 16 |  | 16 |
| tipo_id | descripcion |  | 2 |  | 51 |
| created_at | referencia |  | 8 |  | 21 |
|  | estado |  |  |  | 11 |
|  | created_by |  |  |  | 21 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 240 | Bytes |  |  |  |
| TotalBytesEnTabla | 27.796.892.400 | Bytes |  |  |  |

### TABLA: rol
Para esta tabla por el momento se tienen contemplados 4 roles los cuales son: ADMIN, CAJERO, CLIENTE, AUDITOR. Con estos datos se calcular el tamaño en bytes de los posibles registros en la tabla rol.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | nombre | 1 | 2 | 1 | 16 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 23 | Bytes |  |  |  |
| TotalBytesEnTabla | 92 | Bytes |  |  |  |

### TABLA: usuario
Para esta tabla se asume los usuarios como el medio con el cual una persona se puede autenticar en el sistema, se parte de la base del número de clientes registrados más otros 1000 para los usuarios de la parte administrativa lo que da un total de 38.607.795, con estos datos se calcular el tamaño en bytes de los posibles registros en la tabla usuario.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | username | 3 | 16 | 2 | 21 |
| cliente_id | password_hash |  | 16 |  | 61 |
| activo | mfa_secret |  | 1 |  | 33 |
| intentos_fallidos |  |  | 2 |  |  |
| bloqueado_hasta |  |  | 8 |  |  |
| ultimo_login |  |  | 8 |  |  |
| mfa_activo |  |  | 1 |  |  |
| created_at |  |  | 8 |  |  |
| updated_at |  |  | 8 |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 197 | Bytes |  |  |  |
| TotalBytesEnTabla | 7.605.735.615 | Bytes |  |  |  |

### TABLA: TABLA: token_revocado
Para esta tabla se asume un total de 38.607.795 usuarios de los cuales se tomará el 10% para el cálculo de los posibles registros en la tabla token_revocado lo que da 3.860.779, con estos datos se calcula el tamaño en bytes de los posibles registros en la tabla.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| Id | username | 3 | 16 | 2 | 21 |
| cliente_id | password_hash |  | 16 |  | 61 |
| activo | mfa_secret |  | 1 |  | 33 |
| intentos_fallidos |  |  | 2 |  |  |
| bloqueado_hasta |  |  | 8 |  |  |
| ultimo_login |  |  | 8 |  |  |
| mfa_activo |  |  | 1 |  |  |
| created_at |  |  | 8 |  |  |
| updated_at |  |  | 8 |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 197 | Bytes |  |  |  |
| TotalBytesEnTabla | 760.573.463 | Bytes |  |  |  |


### TABLA: usuario_rol
Para esta tabla se asume un total de 10 roles, con estos datos se calcula el tamaño en bytes de los posibles registros en la tabla.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| usuario_id |  | 0 | 16 | 1 |  |
| rol_id |  |  | 2 |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 19 | Bytes |  |  |  |
| TotalBytesEnTabla | 190 | Bytes |  |  |  |

## BASE DE DATOS AUDIT
### TABLA: flyway_schema_history
Para esta tabla se asume se estima un volumen pequeño y más a largo plazo con un total de 10000 registros en un periodo de 10 años, con estos datos se calcula el tamaño en bytes en la tabla.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| installed_rank | version | 5 | 4 | 2 | 1 |
| checksum | description |  | 4 |  | 18 |
| installed_on | type |  | 8 |  | 3 |
| execution_time | script |  | 4 |  | 28 |
| success | installed_by |  | 1 |  | 8 |
|  |  |  |  |  |  |
| LongitudPorRegistro | 101 | Bytes |  |  |  |
| TotalBytesEnTabla | 1.010.000 | Bytes |  |  |  |

### TABLA: audit_event
Para esta tabla se asume un total de clientes activos de 15.000.000 los cuales realicen 5 operaciones diarias promedio, cada operación genera 8 eventos lo que daría 600.000.000 eventos diarios. Con estos datos se procede a calcular el volumen de bytes aproximado.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| occurred_at | id | 8 | 8 | 2 | 36 |
| created_at | event_id |  | 8 |  | 36 |
|  | event_type |  |  |  | 30 |
|  | aggregate_id |  |  |  | 36 |
|  | correlation_id |  |  |  | 36 |
|  | user_id |  |  |  | 36 |
|  | source_service |  |  |  | 20 |
|  | payload |  |  |  | 500 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 780 | Bytes |  |  |  |
| TotalBytesEnTabla | 468.000.000.000 | Bytes |  |  |  |



## BASE DE DATOS IDENTITY
### TABLA: flyway_schema_history
Para esta tabla se asume y se estima un volumen pequeño y más a largo plazo con un total de 1000 registros en un periodo de 10 años, con estos datos se calcula el tamaño en bytes en la tabla.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| installed_rank | version | 5 | 4 | 2 | 1 |
| checksum | description |  | 4 |  | 18 |
| installed_on | type |  | 8 |  | 3 |
| execution_time | script |  | 4 |  | 28 |
| success | installed_by |  | 1 |  | 8 |
|  |  |  |  |  |  |
| LongitudPorRegistro | 101 | Bytes |  |  |  |
| TotalBytesEnTabla | 101.000 | Bytes |  |  |  |

### TABLA: rol
Para esta tabla se asume un total de 10 roles contemplados para el ingreso y gestión de la BD.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| id | nombre | 2 | 2 | 1 | 15 |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 26 | Bytes |  |  |  |
| TotalBytesEnTabla | 260 | Bytes |  |  |  |


### TABLA: token_revocado
Para esta tabla se asume un total de 5000 ingresos diarios en los cuales se realiza cierre de sesión lo que obliga a generar un token nuevamente.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| id | jti | 1 | 8 | 1 | 36 |
| usuario_id |  |  | 16 |  |  |
| revocado_at |  |  | 8 |  |  |
| expira_at |  |  | 8 |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 81 | Bytes |  |  |  |
| TotalBytesEnTabla | 162.000.000 | Bytes |  |  |  |


### TABLA: usuario_rol
Para esta tabla se asume un total de 1000 asociaciones entre roles y usuarios contemplados para el ingreso y gestión de la BD.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
|  | rol_id | 2 |  | 1 | 15 |
|  | usuario_id |  |  |  | 15 |
|  |  |  |  |  |  |
| LongitudPorRegistro | 39 | Bytes |  |  |  |
| TotalBytesEnTabla | 39.000 | Bytes |  |  |  |


### TABLA: rol_permiso
Para esta tabla se asume un total de 1000 asociaciones entre roles y permisos contemplados para el ingreso y gestión de la BD.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
|  | rol_id | 2 |  | 1 | 15 |
|  | permiso_id |  |  |  | 15 |
|  |  |  |  |  |  |
| LongitudPorRegistro | 39 | Bytes |  |  |  |
| TotalBytesEnTabla | 39.000 | Bytes |  |  |  |

### TABLA: permiso
Para esta tabla se asume un total de 100 posibles permisos para la gestión de la BD.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
|  | id | 3 |  | 1 | 15 |
|  | nombre |  |  |  | 15 |
|  | descripcion |  |  |  | 30 |
|  |  |  |  |  |  |
| LongitudPorRegistro | 73 | Bytes |  |  |  |
| TotalBytesEnTabla | 7.300 | Bytes |  |  |  |

### TABLA: usuario
Para esta tabla se asume un total de clientes activos de 15.000.000 los cuales realicen 5 operaciones diarias promedio, lo que daría 75.000.000 de registros diarios. Con estos datos se procede a calcular el volumen de bytes aproximado.

| Campos longitud fija | Campos longitud variable | # Cam. Long. Variable | Tam (bytes). Camp. Long. Fija | Tam (bytes). Mapa de bits | Tam (bytes). Est. Camp. Long. Variable |
|---|---|---|---|---|---|
| id | username | 3 | 16 | 2 | 20 |
| cliente_id | password_hash |  | 16 |  | 90 |
| activo | mfa_secret |  | 1 |  | 32 |
| intentos_fallidos |  |  | 2 |  |  |
| bloqueado_hasta |  |  | 8 |  |  |
| ultimo_login |  |  | 8 |  |  |
| mfa_activo |  |  | 1 |  |  |
| created_at |  |  | 8 |  |  |
| updated_at |  |  | 8 |  |  |
| bloqueado |  |  | 1 |  |  |
| last_failed_at |  |  | 8 |  |  |
| failed_attempts |  |  | 4 |  |  |
|  |  |  |  |  |  |
| LongitudPorRegistro | 237 | Bytes |  |  |  |
| TotalBytesEnTabla | 17.775.000.000 | Bytes |  |  |  |