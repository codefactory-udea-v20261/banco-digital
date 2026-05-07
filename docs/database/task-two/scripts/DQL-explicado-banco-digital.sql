--Querys segun HU
SELECT * FROM cliente
SELECT * FROM usuario
SELECT * FROM cuenta

--HU-01 - Registro de clientes
--primero necesitamos verificar que el cliente sea nuevo
SELECT EXISTS (
    SELECT 1 
    FROM cliente 
    WHERE numero_cedula = '123'
) AS existe;

--si nos retorna una expresion booleana, true si existe el cliente, false si no existe, esto permite evitar duplicados, la id esta dada por el back end, cuando se desea registrar un nuevo cliente

--HU-02: Consulta de información de cliente
--Esta HU es bastante directa, solo se tiene que definir en base a que vamos a buscar el cliente, en este caso sera a travez de la cedula
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

--HU-03. Actualización de información de cliente
--para esta HU la unica validacion obligatoria es verificar que el cliente exista
SELECT EXISTS (
    SELECT 1 
    FROM cliente 
    WHERE id = 'a1000000-0200-0000-0000-000030000001'
) AS existe;

--HU-04. Crear cuenta financiera
--para esta HU necesitamos de una query ya vista que es la verificacion de que un cliente exista

--tambien necesitamos ver si esta activo 
SELECT EXISTS (
    SELECT 1 
    FROM cliente 
    WHERE id = '1234' AND activo = true
) AS cliente_valido;

--y por ultimo verificar que la cuenta no exista para no repetirla
SELECT EXISTS (
    SELECT 1 
    FROM cuenta 
    WHERE numero_cuenta = '123'
) AS existe;

--HU-06. Consultar saldo
--para esta HU primero necesitamos verificar que la cuenta si le pertenezca al cliente
--ademos de verificar que este activa
SELECT cu.saldo
FROM cuenta cu
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE cu.numero_cuenta = '123'
AND u.id = 'c3000000-1000-2000-3000-400000000003'
AND cu.estado = 'ACTIVA';

--HU-07. Transferir dinero
--Toda transferencia debe tener un beggin y un commit
BEGIN;
-- queries
--primero validamos la cuenta origen, osea a quien le pertence y el estado en el que esta
SELECT cu.id, cl.saldo
FROM cuenta cu
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE cu.numero_cuenta = '123'
AND u.id = 'c3000020-3000-4000-5000-000000000001'
AND cu.estado = 'ACTIVA'
FOR UPDATE;

--luego validamos la cuenta destino
SELECT id
FROM cuenta
WHERE numero_cuenta = '456'
AND estado = 'ACTIVA'
FOR UPDATE;

--tras estas verificaciones se continuaria con la logica de transaccion, que no es una query direc
COMMIT;

--HU-08.  Retiro de dinero de mi cuenta
--Esta HU es bastante similar a la anterior solo que mas simple, tambien requiere de begin y commit
BEGIN;
-- queries
--verificamos la informacion de quien va a retirar
--osea si la cuenta es verdaderamente suya y el estado de la cuenta
SELECT cu.id, cl.saldo
FROM cuenta cu
JOIN usuario u ON u.cliente_id = cu.cliente_id
WHERE cu.numero_cuenta = '123'
AND u.id = 'c3000020-3000-4000-5000-000000000001'
AND cu.estado = 'ACTIVA'
FOR UPDATE;
--tras esto seguiria la logica de retiro que no es un query en si
COMMIT;

--HU-09. Consultar historial de operaciones

--esta HU es bastante directa, podemos sacar la informacion de un query
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

--HU-10. Reporte de actividad de mis cuentas
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
                     AND '2026-04-30 23:59:59'


--HU-11: Login y Logout
--Se usa una query para validar que la informacion ingresada coincide, esto en base a una credencial, en este caso el nombre de usuario
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

--dependiendo de como lo queramos trabajar podemos verificar si un usuario esta bloqueado desde la base de datos con el siguiente query
SELECT bloqueado_hasta
FROM usuario
WHERE username = 'asesor@banco.com';

--si se llegasen a necesitar los datos del cliente y no del usuario tendriamos que usar la siguiente query
SELECT 
    cl.id,
    cl.primer_nombre,
    cl.primer_apellido,
    cl.numero_cedula
FROM cliente cl
JOIN usuario u ON u.cliente_id = cl.id
WHERE u.username = 'ejemplo@test.com';

--si se quiere validar la existencia de un usuario se usa el siguiente query
SELECT EXISTS (
    SELECT 1 
    FROM usuario 
    WHERE username = 'ejemplo@test.com'
);
