Feature: Gestión de clientes y cuentas

  # HU01 
  @CPHU01-01
  Scenario: Registro exitoso de un nuevo cliente
    Given no existe un cliente con email "ana@test.com" ni cedula "1020304050"
    When el asesor crea un cliente con nombre "Ana" apellido "Torres" email "ana@test.com" cedula "1020304050" telefono "3001234567"
    Then el cliente queda registrado exitosamente
    And el sistema dispara el evento de dominio ClienteRegistrado

  @CPHU01-02
  Scenario: Registro fallido por email ya registrado
    Given ya existe un cliente con email "ana@test.com"
    When el asesor intenta crear otro cliente con el mismo email "ana@test.com"
    Then el sistema lanza ClienteYaExisteException

  @CPHU01-03
  Scenario: Registro fallido por cedula ya registrada
    Given ya existe un cliente con cedula "1020304050"
    When el asesor intenta crear otro cliente con cedula "1020304050" y email "nuevo@test.com"
    Then el sistema lanza ClienteYaExisteException

  #  HU02 
  Scenario: Consultar cliente registrado exitosamente
    Given existe un cliente registrado con un id conocido
    When el asesor consulta el cliente por ese id
    Then el sistema retorna los datos del cliente con nombre y email

  @CPHU02-02
  Scenario: Consultar cliente que no existe
    Given no existe ningun cliente con el id consultado
    When el asesor consulta el cliente por ese id inexistente
    Then el sistema lanza ClienteNoEncontradoException

  # HU03
  @CPHU03-01
  Scenario: Actualizar datos del cliente exitosamente
    Given existe un cliente con nombre "Juan" y email "juan@test.com"
    When el asesor actualiza el primer nombre a "Carlos"
    Then el cliente queda actualizado con nombre "Carlos"
    And el sistema dispara el evento de dominio ClienteActualizado con campo "primerNombre"

  @CPHU03-02
  Scenario: Actualizar email con uno ya existente en otro cliente
    Given existe un cliente con id conocido y email "juan@test.com"
    And existe otro cliente con email "existente@test.com"
    When el asesor intenta cambiar el email del primer cliente a "existente@test.com"
    Then el sistema lanza ClienteYaExisteException

  #  HU04 
  @CPHU04-01
  Scenario: Crear cuenta para cliente existente y activo
    Given el cliente existe y esta activo
    When el asesor crea una cuenta de tipo "AHORRO" para ese cliente
    Then la cuenta queda creada con estado "ACTIVA"
    And el numero de cuenta comienza con "CTA-"

  @CPHU04-02
  Scenario: Crear cuenta para cliente inexistente
    Given el cliente no existe en el sistema
    When el asesor intenta crear una cuenta de tipo "AHORRO" para ese cliente
    Then el sistema lanza ClienteNoEncontradoException

  @CPHU04-03
  Scenario: Crear cuenta para cliente inactivo
    Given el cliente existe pero esta inactivo
    When el asesor intenta crear una cuenta de tipo "AHORRO" para ese cliente
    Then el sistema lanza ClienteInactivoException

  #  HU05 
  @CPHU05-01
  Scenario: Consultar cuentas de cliente con cuentas registradas
    Given existe un cliente con una cuenta registrada de tipo "AHORRO"
    When se consultan las cuentas de ese cliente
    Then la respuesta contiene al menos una cuenta

  @CPHU05-02
  Scenario: Consultar cuentas de cliente sin cuentas
    Given existe un cliente sin ninguna cuenta registrada
    When se consultan las cuentas de ese cliente
    Then la respuesta es una lista vacia

  # HU06 
  @CPHU06-01
  Scenario: Consulta exitosa del saldo de cuenta activa
    Given existe una cuenta activa con saldo "100000" perteneciente al cliente
    When el cliente consulta el saldo de esa cuenta
    Then el sistema retorna el saldo "100000"

  @CPHU06-02
  Scenario: Consulta de saldo de cuenta inactiva
    Given existe una cuenta inactiva perteneciente al cliente
    When el cliente intenta consultar el saldo de esa cuenta
    Then el saldo de la cuenta inactiva no puede consultarse

  @CPHU06-03
  Scenario: Consulta de saldo de cuenta que no pertenece al cliente
    Given existe una cuenta activa perteneciente a otro cliente
    When el cliente intenta consultar el saldo de esa cuenta
    Then el sistema lanza CuentaNoPerteneceAlClienteException