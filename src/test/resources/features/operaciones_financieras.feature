Feature: Operaciones financieras e historial

  #  HU07 
  @CPHU07-01
  Scenario: Transferencia exitosa entre dos cuentas activas
    Given la cuenta origen "123" esta activa con saldo "100.0"
    And la cuenta destino "456" esta activa con saldo "20.0"
    When se transfieren "50.0" de la cuenta "123" a la cuenta "456"
    Then el saldo de la cuenta "123" queda en "50.0"
    And el saldo de la cuenta "456" queda en "70.0"
    And se guardan exactamente 2 transacciones en el repositorio

  @CPHU07-02
  Scenario: Transferencia fallida por saldo insuficiente
    Given la cuenta origen "123" esta activa con saldo "100.0"
    And la cuenta destino "456" esta activa con saldo "20.0"
    When se intenta transferir "500.0" de la cuenta "123" a la cuenta "456"
    Then el sistema lanza SaldoInsuficienteException
    And no se guarda ninguna transaccion ni se modifica ningun saldo

  @CPHU07-03
  Scenario: Transferencia a cuenta destino inexistente
    Given la cuenta origen "123" esta activa con saldo "100.0"
    And la cuenta destino "999-9999" no existe en el sistema
    When se intenta transferir "50.0" de la cuenta "123" a la cuenta "999-9999"
    Then el sistema lanza TransferenciaInvalidaException con mensaje "destino no existe"

  @CPHU07-04
  Scenario: Transferencia desde cuenta origen bloqueada
    Given la cuenta origen "123" esta en estado "BLOQUEADA"
    And la cuenta destino "456" existe en el sistema
    When se intenta transferir "50.0" de la cuenta "123" a la cuenta "456"
    Then el sistema lanza CuentaInactivaException

  @CPHU07-04b
  Scenario: Transferencia a cuenta destino bloqueada
    Given la cuenta origen "123" esta activa con saldo "100.0"
    And la cuenta destino "456" esta en estado "BLOQUEADA"
    When se intenta transferir "50.0" de la cuenta "123" a la cuenta "456"
    Then el sistema lanza CuentaInactivaException

  @CPHU07-05
  Scenario: Transferencia a la misma cuenta
    When se intenta transferir "50.0" de la cuenta "001-2025" a la misma cuenta "001-2025"
    Then el sistema lanza TransferenciaInvalidaException con mensaje "origen y destino"

  @CPHU07-06
  Scenario: Transferencia con monto negativo
    When se intenta transferir "-50.0" de la cuenta "123" a la cuenta "456"
    Then el sistema lanza TransferenciaInvalidaException con mensaje "mayor a cero"

  #  HU08 
  @CPHU08-01
  Scenario: Retiro exitoso de dinero
    Given la cuenta tiene saldo disponible de "100.0"
    When el cliente solicita un retiro de "50.0"
    Then el saldo se actualiza restando "50.0"
    And la transaccion queda registrada con una referencia no nula de maximo 50 caracteres

  @CPHU08-02
  Scenario: Retiro fallido por saldo insuficiente
    Given la cuenta tiene saldo disponible de "100.0"
    When el cliente solicita un retiro de "150.0"
    Then el sistema lanza SaldoInsuficienteException
    And no se guarda ninguna transaccion ni se modifica ningun saldo

  @CPHU08-03
  Scenario: Retiro de cuenta inexistente
    Given la cuenta no existe en el sistema de retiro
    When el cliente intenta realizar un retiro de "50.0"
    Then el sistema lanza CuentaTransaccionException

  #  HU09 
  @CPHU09-01
  Scenario: Consulta exitosa del historial con movimientos
    Given la cuenta tiene un movimiento registrado de tipo "RETIRO"
    When el cliente consulta el historial de la cuenta
    Then la lista de movimientos tiene 1 elemento con tipo "RETIRO"

  @CPHU09-02
  Scenario: Consulta de historial sin movimientos registrados
    Given la cuenta no tiene ningun movimiento registrado
    When el cliente consulta el historial de la cuenta
    Then la lista de movimientos esta vacia

 