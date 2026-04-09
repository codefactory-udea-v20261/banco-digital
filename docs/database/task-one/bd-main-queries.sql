-- En un banco una de las cosas mas importantes es saber cuanto dinero tengo en una cuenta, esto se podría hacer fácilmente con la siguiente consulta
SELECT saldo FROM cuenta
WHERE numero_cuenta = ‘numero_de_cuenta’;
--Donde el parámetro de ‘numero_de_cuenta’ es dado por el back

-- Otra consulta que puede ser necesaria es traer todos los usuario, así como sus roles, ya sea para hacer un listado o una documentación
SELECT u.username, r.nombre AS rol FROM usuario u
JOIN usuario_rol ur ON ur.usuario_id=u.id
JOIN rol r ON ur.rol_id=r.id
ORDER BY u.username;

-- También podemos consultar el rol según el id del usuario en caso de que necesitemos hacer una verificación
SELECT u.id AS usuario_id, u.username, r.nombre AS rol FROM usuario u
JOIN usuario_rol ur ON ur.usuario_id = u.id
JOIN rol r ON ur.rol_id = r.id
WHERE u.id = 'UUID_DEL_USUARIO';
--Donde ‘UUID_DEL_USUARIO’ esta dado por el back-end

--Otra consulta que puede ser muy útil y es bastante general es traer distinta información sobre todas las cuentas de un usuario, por ejemplo, el usuario, el numero de cuenta, el saldo, el estado de la cuenta para darle al cliente un registro de sus cuentas, esto se puede hacer con la siguiente consulta, la ‘UUID_USUARIO’ estará dada por el back-end
SELECT u.username, cta.numero_cuenta, cta.saldo, cta.estado FROM usuario u
JOIN cliente cl ON u.cliente_id = cl.id
JOIN cuenta cta ON cta.cliente_id = cl.id
WHERE u.id = 'UUID_USUARIO';
--Una consulta básica que se puede tener es obtener el tipo de cuenta de todas las cuentas de un cliente, esto lo podemos hacer fácilmente con la UUID del cliente, la consulta para realizar esta acción seria la siguiente
SELECT u.username, cta.numero_cuenta, tc.nombre AS tipo_cuenta FROM usuario u
JOIN cliente cl ON u.cliente_id = cl.id
JOIN cuenta cta ON cta.cliente_id = cl.id
JOIN tipo_cuenta tc ON cta.tipo_cuenta_id = tc.id
WHERE cl.id = ‘UUID_DEL_CLIENTE’;
-- Donde ‘UUID_DEL_CLIENTE’ esta dado por el back-end

-- Una consulta muy útil para temas de seguridad es obtener el ultimo inicio de sesión que un usuario puede tener, es fácil de sacar con la siguiente consulta.
SELECT username, ultimo_login FROM usuario
WHERE id = 'UUID_DEL_USUARIO';
--Donde ‘UUID_DEL_USUARIO’ esta dado por el back-end