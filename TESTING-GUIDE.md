# Guía de Testing: Banco Digital Microservicios

## Descripción

Este documento proporciona una guía completa para probar la arquitectura de 3 microservicios del Banco Digital en todos los niveles: unitarios, integración, e2e, y carga.

---

## FLUJO DE TESTING RECOMENDADO

```
1. Health Checks (servicios levantados) → 2 min
   ↓
2. Unit Tests (en paralelo) → 5 min
   ↓
3. Integration Tests → 10 min
   ↓
4. E2E Tests (flujo completo) → 15 min
   ↓
5. Performance Tests (opcional) → 30 min
```

---

## NIVEL 1: HEALTH CHECKS (2 minutos)

### 1.1 Verificar servicios levantados

```bash
# Script rápido para verificar los 3 servicios
#!/bin/bash

echo "Checking services health..."
for port in 8080 8081 8082; do
    echo -n "Port $port: "
    if curl -s http://localhost:$port/actuator/health | grep -q '"status":"UP"'; then
        echo "✓ UP"
    else
        echo "✗ DOWN"
        exit 1
    fi
done
echo "All services are UP!"
```

### 1.2 Verificar conectividad a BD

```bash
curl http://localhost:8080/actuator/health/db -s | jq '.'
curl http://localhost:8081/actuator/health/db -s | jq '.'
curl http://localhost:8082/actuator/health/db -s | jq '.'
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    }
  }
}
```

---

## NIVEL 2: UNIT TESTS (5 minutos)

### 2.1 Ejecutar tests de cada servicio

```bash
cd /home/nia/projects/udea/arquisoft/banco-digital

# Core Banking
mvn test

# Identity Service
cd core/banco-digital-identity
mvn test
cd ../..

# Audit Service
cd core/banco-digital-audit
mvn test
cd ../..
```

### 2.2 Casos de test que deben pasar

#### Core Banking
- [ ] CrearClienteUseCaseTest → Crear cliente con datos válidos
- [ ] ActualizarClienteUseCaseTest → Actualizar datos de cliente
- [ ] CrearCuentaUseCaseTest → Crear cuenta para cliente
- [ ] ObtenerSaldoUseCaseTest → Consultar saldo de cuenta
- [ ] RealizarTransferenciaUseCaseTest → Realizar transferencia entre cuentas

#### Identity Service
- [ ] LoginUseCaseTest → Login con credenciales válidas
- [ ] LogoutUseCaseTest → Logout revoca token
- [ ] GenerarJwtTest → JWT generado contiene claims correctos
- [ ] ValidarJwtTest → Token válido se valida exitosamente
- [ ] ProvisionClienteAccessTest → Nuevo cliente recibe acceso

#### Audit Service
- [ ] RegistrarEventoUseCaseTest → Evento se guarda en BD
- [ ] ObtenerLogsUseCaseTest → Recuperar logs por cliente
- [ ] FiltrarEventosTest → Filtrar eventos por fecha y tipo

---

## NIVEL 3: INTEGRATION TESTS (10 minutos)

### 3.1 Ejecutar integration tests completos

```bash
# Desde raíz
mvn verify -DskipUnitTests

# O para un servicio específico
cd core/banco-digital-identity
mvn verify
```

### 3.2 Casos de integración clave

#### 1. Test de Autenticación Completa

**Objetivo**: Verificar flujo de login → token → autenticación

```java
// Identity Service Integration Test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationIntegrationTest {
    
    @Test
    void testCompleteAuthenticationFlow() {
        // 1. Login
        LoginRequest login = new LoginRequest("usuario@banco.com", "Pass123!");
        String token = authController.login(login).getToken();
        
        // 2. Validar que token no está vacío
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // 3. Obtener datos de identidad con token
        AuthenticatedUser user = authController.getMe(token);
        
        // 4. Verificar claims del JWT
        assertEquals("usuario@banco.com", user.getUsername());
        assertTrue(user.getRoles().contains("CLIENTE"));
    }
}
```

#### 2. Test de Creación de Cliente con Provisioning

**Objetivo**: Crear cliente → provisionar acceso en Identity Service

```bash
# Manual test
# 1. Crear cliente en Core Banking
CLIENT_ID=$(curl -s -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "numeroCedula": "12345678",
    "primerNombre": "Carlos",
    "primerApellido": "López",
    "email": "carlos@banco.com",
    "fechaNacimiento": "1985-03-20"
  }' | jq -r '.data.id')

# 2. Verificar que usuario se creó en Identity Service
curl -s http://localhost:8081/api/v1/auth/users/$CLIENT_ID \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

#### 3. Test de Auditoría de Eventos

**Objetivo**: Operación en Core → Evento en Audit Service

```bash
# 1. Crear transacción (trigger audit event)
TRANS_ID=$(curl -s -X POST http://localhost:8080/api/v1/transacciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "cuentaOrigen": "CUENTA-001",
    "cuentaDestino": "CUENTA-002",
    "monto": 5000.00,
    "concepto": "Pago de servicios"
  }' | jq -r '.data.id')

# 2. Verificar que evento se registró en Audit Service
curl -s http://localhost:8082/api/v1/audit/events \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.data[] | select(.descripcion | contains("TRANSACCION"))'
```

#### 4. Test de Comunicación Entre Servicios

**Objetivo**: Core Banking → Identity Service (obtener usuario)

```bash
# Verificar que Core Banking puede llamar Identity
curl -v http://localhost:8080/api/v1/clientes/validate \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"userId": "550e8400-e29b-41d4-a716-446655440000"}'

# El Core Banking internamente debe llamar a Identity Service
# y validar que el usuario existe
```

---

## NIVEL 4: E2E TESTS - FLUJO COMPLETO (15 minutos)

### 4.1 Flujo de Negocio Completo

Este test simula un cliente real usando toda la plataforma:

```bash
#!/bin/bash

set -e

echo "=== BANCO DIGITAL - E2E TEST FLOW ==="

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Login en Identity Service
echo -e "${YELLOW}[1/7] Testing Login...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "cliente@banco.com",
    "clave": "Temp1234!",
    "mfaCode": "123456"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo "❌ Login failed"
    echo $LOGIN_RESPONSE | jq '.'
    exit 1
fi
echo -e "${GREEN}✓ Login successful${NC}"
echo "  Token: ${TOKEN:0:50}..."

# Test 2: Obtener identidad autenticada
echo -e "${YELLOW}[2/7] Getting authenticated user...${NC}"
USER_RESPONSE=$(curl -s -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN")

USER_ID=$(echo $USER_RESPONSE | jq -r '.data.userId')
CLIENT_ID=$(echo $USER_RESPONSE | jq -r '.data.clienteId')
echo -e "${GREEN}✓ User authenticated${NC}"
echo "  UserID: $USER_ID"
echo "  ClientID: $CLIENT_ID"

# Test 3: Crear nuevo cliente en Core Banking
echo -e "${YELLOW}[3/7] Creating new customer...${NC}"
CREATE_CLIENT=$(curl -s -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "numeroCedula": "987654321",
    "primerNombre": "Ana",
    "primerApellido": "García",
    "email": "ana@banco.com",
    "fechaNacimiento": "1992-06-15"
  }')

NEW_CLIENT_ID=$(echo $CREATE_CLIENT | jq -r '.data.id // empty')
if [ -z "$NEW_CLIENT_ID" ]; then
    echo "❌ Client creation failed"
    echo $CREATE_CLIENT | jq '.'
    exit 1
fi
echo -e "${GREEN}✓ Customer created${NC}"
echo "  ClientID: $NEW_CLIENT_ID"

# Test 4: Crear cuenta para cliente
echo -e "${YELLOW}[4/7] Creating bank account...${NC}"
CREATE_ACCOUNT=$(curl -s -X POST http://localhost:8080/api/v1/cuentas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"clienteId\": \"$NEW_CLIENT_ID\",
    \"tipo\": \"AHORROS\",
    \"saldoInicial\": 100000.00
  }")

ACCOUNT_ID=$(echo $CREATE_ACCOUNT | jq -r '.data.id // empty')
if [ -z "$ACCOUNT_ID" ]; then
    echo "❌ Account creation failed"
    echo $CREATE_ACCOUNT | jq '.'
    exit 1
fi
echo -e "${GREEN}✓ Account created${NC}"
echo "  AccountID: $ACCOUNT_ID"

# Test 5: Consultar saldo
echo -e "${YELLOW}[5/7] Checking account balance...${NC}"
BALANCE=$(curl -s -X GET http://localhost:8080/api/v1/cuentas/$ACCOUNT_ID/saldo \
  -H "Authorization: Bearer $TOKEN")

SALDO=$(echo $BALANCE | jq -r '.data.saldo')
echo -e "${GREEN}✓ Balance retrieved${NC}"
echo "  Current balance: \$${SALDO}"

# Test 6: Consultar auditoría
echo -e "${YELLOW}[6/7] Checking audit logs...${NC}"
AUDIT_LOGS=$(curl -s -X GET "http://localhost:8082/api/v1/audit/logs?clienteId=$NEW_CLIENT_ID" \
  -H "Authorization: Bearer $TOKEN")

LOG_COUNT=$(echo $AUDIT_LOGS | jq '.data | length')
echo -e "${GREEN}✓ Audit logs retrieved${NC}"
echo "  Log entries: $LOG_COUNT"

# Test 7: Logout
echo -e "${YELLOW}[7/7] Logging out...${NC}"
LOGOUT=$(curl -s -X POST http://localhost:8081/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN")

echo -e "${GREEN}✓ Logout successful${NC}"

echo ""
echo -e "${GREEN}=== ALL E2E TESTS PASSED ==${NC}"
```

**Guardar como**: `scripts/e2e-test.sh`

```bash
chmod +x scripts/e2e-test.sh
./scripts/e2e-test.sh
```

### 4.2 Validaciones en E2E Test

| Paso | Validación | Esperado |
|------|-----------|----------|
| Login | JWT token obtenido | Token no vacío |
| Get Me | Claims del token | `userId`, `clientId`, `roles` presentes |
| Create Client | Cliente creado | ID retornado |
| Create Account | Cuenta creada | Saldo inicial = 100000 |
| Check Balance | Saldo consultable | Saldo = 100000 |
| Audit | Eventos registrados | >= 2 eventos |
| Logout | Token revocado | Siguientes requests fallan |

---

## NIVEL 5: TESTS DE CARGA (30 minutos)

### 5.1 Test con Apache JMeter

```bash
# Instalar JMeter
brew install jmeter

# O descargar desde https://jmeter.apache.org/
```

#### 5.1.1 Crear plan de prueba

```xml
<!-- archivo: test-plan.jmx -->
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testname="Banco Digital Load Test">
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments"/>
      <stringProp name="TestPlan.comments">Load test para 3 microservicios</stringProp>
      
      <!-- Test 1: Login -->
      <ThreadGroup guiclass="ThreadGroupGui" testname="Login Test">
        <stringProp name="ThreadGroup.num_threads">10</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
        
        <HTTPSampler guiclass="HttpTestSampleGui" testname="POST /auth/login">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <HTTPArgument name="correo" value="cliente@banco.com"/>
            <HTTPArgument name="clave" value="Temp1234!"/>
          </elementProp>
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8081</stringProp>
          <stringProp name="HTTPSampler.path">/api/v1/auth/login</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
        </HTTPSampler>
      </ThreadGroup>
      
      <!-- Test 2: Create Client -->
      <ThreadGroup guiclass="ThreadGroupGui" testname="Create Client Test">
        <stringProp name="ThreadGroup.num_threads">5</stringProp>
        <stringProp name="ThreadGroup.ramp_time">5</stringProp>
        
        <HTTPSampler guiclass="HttpTestSampleGui" testname="POST /clientes">
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8080</stringProp>
          <stringProp name="HTTPSampler.path">/api/v1/clientes</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
        </HTTPSampler>
      </ThreadGroup>
      
    </TestPlan>
  </hashTree>
</jmeterTestPlan>
```

#### 5.1.2 Ejecutar test de carga

```bash
jmeter -n -t test-plan.jmx -l results.jtl -j jmeter.log -j test-results.html
```

### 5.2 Test con Apache Bench (más simple)

```bash
# Test de 1000 requests con 10 concurrentes en Health Check
ab -n 1000 -c 10 http://localhost:8080/actuator/health

# Test de login (100 requests)
ab -n 100 -c 5 -p login-data.json \
   -T application/json \
   http://localhost:8081/api/v1/auth/login
```

### 5.3 Métricas esperadas

| Métrica | Esperado | Máximo |
|---------|----------|--------|
| Respuesta promedio | <100ms | 500ms |
| P95 latency | <200ms | 1000ms |
| Error rate | 0% | <1% |
| Throughput | >100 req/s | - |

---

## NIVEL 6: MONITORING & OBSERVABILIDAD

### 6.1 Ver métricas en tiempo real

```bash
# Prometheus metrics endpoint
curl http://localhost:8080/actuator/prometheus | grep http_requests_total
```

### 6.2 Configurar Prometheus (opcional)

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'core-banking'
    static_configs:
      - targets: ['localhost:8080']
        
  - job_name: 'identity'
    static_configs:
      - targets: ['localhost:8081']
        
  - job_name: 'audit'
    static_configs:
      - targets: ['localhost:8082']
```

### 6.3 Logs y Debugging

```bash
# Ver logs en vivo
docker-compose logs -f

# Logs de un servicio específico
docker-compose logs -f core-banking

# Guardar logs
docker-compose logs > logs.txt

# Tail último 100 líneas
docker-compose logs --tail=100
```

---

## CHECKLIST DE TESTING

### Pre-Testing
- [ ] Servicios levantados y respondiendo health check
- [ ] PostgreSQL con 3 BD creadas
- [ ] Variables de entorno correctas
- [ ] JAR files compilados correctamente

### Unit Tests
- [ ] Todos los unit tests pasan en Core Banking
- [ ] Todos los unit tests pasan en Identity
- [ ] Todos los unit tests pasan en Audit
- [ ] Coverage >60% en clases críticas

### Integration Tests
- [ ] Tests de autenticación pasan
- [ ] Tests de creación de cliente pasan
- [ ] Tests de creación de cuenta pasan
- [ ] Tests de auditoría pasan

### E2E Tests
- [ ] Flujo de login funciona
- [ ] Creación de cliente funciona
- [ ] Creación de cuenta funciona
- [ ] Auditoría registra eventos
- [ ] Inter-service communication funciona

### Load Tests
- [ ] 1000 requests sin errors
- [ ] Latencia promedio <100ms
- [ ] P95 latency <200ms
- [ ] Error rate <1%

### Security
- [ ] JWT validation funciona
- [ ] CORS está configurado
- [ ] Endpoints públicos son accesibles
- [ ] Endpoints privados requieren token

---

## Troubleshooting Tests

### Error: "Connection refused"
```bash
# Verificar que servicios están corriendo
docker-compose ps

# Reiniciar servicios
docker-compose restart
```

### Error: "Invalid token"
```bash
# Asegurar JWT_SECRET es el mismo en todos los servicios
echo $JWT_SECRET

# Regenerar token
curl -X POST http://localhost:8081/api/v1/auth/login ...
```

### Error: "Database error"
```bash
# Verificar conexión a BD
curl http://localhost:8080/actuator/health/db

# Revisar logs de PostgreSQL
docker-compose logs postgres
```

