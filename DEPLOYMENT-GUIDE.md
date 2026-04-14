# Guía de Despliegue: Arquitectura de Microservicios

## Descripción General

El proyecto **Banco Digital** está implementado como una arquitectura de **3 microservicios independientes**, cada uno ejecutándose en su propio puerto con su propia base de datos (esquema):

| Servicio | Puerto | Base de Datos | Descripción |
|----------|--------|---------------|-------------|
| **Core Banking** | 8080 | `banco_digital_core` | Gestión de clientes, cuentas y transacciones |
| **Identity Service** | 8081 | `banco_digital_identity` | Autenticación, JWT, gestión de usuarios |
| **Audit Service** | 8082 | `banco_digital_audit` | Registro de eventos y auditoría |

---

## OPCIÓN 1: Despliegue Local (Desarrollo)

### Requisitos Previos
- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16 (si quieres ejecutar sin Docker)

### Método A: Con Docker Compose (Recomendado)

#### 1. Compilar todos los servicios

```bash
cd /home/nia/projects/udea/arquisoft/banco-digital

# Compilar Core Banking
mvn clean package -DskipTests

# Compilar Identity Service
cd core/banco-digital-identity
mvn clean package -DskipTests
cd ../..

# Compilar Audit Service
cd core/banco-digital-audit
mvn clean package -DskipTests
cd ../..
```

#### 2. Iniciar con Docker Compose

```bash
# Desde el directorio raíz del proyecto
docker-compose up -d

# Esperar a que PostgreSQL esté listo (~15 segundos)
# Luego los 3 servicios se iniciarán automáticamente
```

#### 3. Verificar que los servicios estén corriendo

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# O ver logs específicos de un servicio
docker-compose logs -f core-banking
docker-compose logs -f identity
docker-compose logs -f audit

# Verificar containers corriendo
docker ps | grep banco-digital
```

#### 4. Acceder a los servicios

| Servicio | Swagger UI | Health Check |
|----------|-----------|--------------|
| Core Banking | http://localhost:8080/swagger-ui.html | http://localhost:8080/actuator/health |
| Identity | http://localhost:8081/swagger-ui.html | http://localhost:8081/actuator/health |
| Audit | http://localhost:8082/swagger-ui.html | http://localhost:8082/actuator/health |

#### 5. Detener los servicios

```bash
docker-compose down

# Borrar volumenes (datos) también
docker-compose down -v
```

---

### Método B: Ejecutar Localmente sin Docker

#### 1. Instalar y configurar PostgreSQL

```bash
# Linux/Mac
brew install postgresql@16

# O usando Docker solo para PostgreSQL
docker run -d \
  --name banco-digital-postgres \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_MULTIPLE_DATABASES=banco_digital_core,banco_digital_identity,banco_digital_audit \
  -p 5432:5432 \
  postgres:16-alpine
```

#### 2. Compilar servicios

```bash
cd /home/nia/projects/udea/arquisoft/banco-digital

# Compilar todos
mvn clean package -DskipTests
cd core/banco-digital-identity && mvn clean package -DskipTests && cd ../..
cd core/banco-digital-audit && mvn clean package -DskipTests && cd ../..
```

#### 3. Crear las bases de datos

```bash
psql -U postgres -c "CREATE DATABASE banco_digital_core;"
psql -U postgres -c "CREATE DATABASE banco_digital_identity;"
psql -U postgres -c "CREATE DATABASE banco_digital_audit;"
```

#### 4. Iniciar cada servicio en una terminal separada

**Terminal 1: Core Banking**
```bash
cd /home/nia/projects/udea/arquisoft/banco-digital
java -jar target/banco-digital-core-banking-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/banco_digital_core \
  --server.port=8080
```

**Terminal 2: Identity Service**
```bash
cd /home/nia/projects/udea/arquisoft/banco-digital/core/banco-digital-identity
java -jar target/banco-digital-identity-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/banco_digital_identity \
  --server.port=8081
```

**Terminal 3: Audit Service**
```bash
cd /home/nia/projects/udea/arquisoft/banco-digital/core/banco-digital-audit
java -jar target/banco-digital-audit-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/banco_digital_audit \
  --server.port=8082
```

---

## OPCIÓN 2: Despliegue en Servidor (Staging/Production)

### Configurar variables de entorno

Crear archivo `.env` en el directorio raíz:

```bash
# Database
DB_HOST=postgres.example.com
DB_PORT=5432
DB_USERNAME=banco_digital_user
DB_PASSWORD=secure-password-here-change-in-prod

# JWT
JWT_SECRET=your-256-bit-secret-key-change-in-production
JWT_EXPIRATION_MS=3600000

# CORS
CORS_ALLOWED_ORIGINS=https://frontend.example.com,https://app.example.com

# Service URLs (for inter-service communication)
IDENTITY_SERVICE_URL=https://identity.example.com
AUDIT_SERVICE_URL=https://audit.example.com

# Application Profile
APP_PROFILE=production
```

### Actualizar docker-compose para producción

```bash
# Opción A: Usar docker-compose con archivo de override
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Opción B: Usar env file
docker-compose --env-file .env up -d
```

### Configurar NGINX como proxy inverso

```nginx
# /etc/nginx/conf.d/banco-digital.conf

upstream core_banking {
    server localhost:8080;
}

upstream identity {
    server localhost:8081;
}

upstream audit {
    server localhost:8082;
}

server {
    listen 80;
    server_name api.banco-digital.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.banco-digital.com;
    
    ssl_certificate /etc/ssl/banco-digital.crt;
    ssl_certificate_key /etc/ssl/banco-digital.key;
    
    # Core Banking
    location /api/v1/clientes {
        proxy_pass http://core_banking;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /api/v1/cuentas {
        proxy_pass http://core_banking;
    }
    
    # Identity Service
    location /api/v1/auth {
        proxy_pass http://identity;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # Audit Service
    location /api/v1/audit {
        proxy_pass http://audit;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## PRUEBAS DE FUNCIONALIDAD

### 1. Verificar que los servicios estén levantados

```bash
# Todos los servicios deben retornar {"status":"UP"}
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

### 2. Test de autenticación (Identity Service)

#### 2.1 Login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "cliente@banco.com",
    "clave": "Temp1234!",
    "mfaCode": "123456"
  }'

# Respuesta esperada:
# {
#   "success": true,
#   "data": {
#     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
#   }
# }
```

#### 2.2 Obtener identidad autenticada

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"

# Respuesta esperada:
# {
#   "userId": "550e8400-e29b-41d4-a716-446655440000",
#   "username": "cliente@banco.com",
#   "clienteId": "550e8400-e29b-41d4-a716-446655440001",
#   "roles": ["CLIENTE"]
# }
```

### 3. Test de Core Banking Service

#### 3.1 Crear cliente

```bash
curl -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "numeroCedula": "123456789",
    "primerNombre": "Juan",
    "primerApellido": "Pérez",
    "email": "juan@example.com",
    "fechaNacimiento": "1990-01-15"
  }'
```

#### 3.2 Obtener cliente

```bash
curl -X GET http://localhost:8080/api/v1/clientes/{clienteId} \
  -H "Authorization: Bearer $TOKEN"
```

#### 3.3 Crear cuenta

```bash
curl -X POST http://localhost:8080/api/v1/cuentas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "clienteId": "550e8400-e29b-41d4-a716-446655440000",
    "tipo": "AHORROS",
    "saldoInicial": 100000.00
  }'
```

### 4. Test de Audit Service

#### 4.1 Obtener logs de auditoría

```bash
curl -X GET "http://localhost:8082/api/v1/audit/logs?clienteId=550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN"
```

#### 4.2 Registrar evento manualmente

```bash
curl -X POST http://localhost:8082/api/v1/audit/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "tipoEvento": "CLIENTE_REGISTRADO",
    "clienteId": "550e8400-e29b-41d4-a716-446655440000",
    "detalles": {
      "email": "juan@example.com",
      "accion": "Nuevo cliente registrado"
    }
  }'
```

---

## Testing Automatizado

### 1. Unit Tests

```bash
cd /home/nia/projects/udea/arquisoft/banco-digital
mvn test

cd core/banco-digital-identity
mvn test

cd ../core/banco-digital-audit
mvn test
```

### 2. Integration Tests

```bash
mvn verify
```

### 3. Test con Postman/Insomnia

1. Importar la colección desde `postman/` en el proyecto
2. Configurar variables de entorno:
   - `BASE_URL`: `http://localhost:8080`
   - `IDENTITY_URL`: `http://localhost:8081`
   - `AUDIT_URL`: `http://localhost:8082`
   - `TOKEN`: Token obtenido del login

---

## Monitoreo

### Prometheus Metrics

```bash
# Endpoints de métricas (Prometheus)
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8082/actuator/prometheus
```

### Logs

```bash
# En Docker
docker-compose logs -f core-banking

# Localmente
tail -f target/logs/banco-digital.log
```

### Health Checks

```bash
# Full health
curl http://localhost:8080/actuator/health/

# Database connection
curl http://localhost:8080/actuator/health/db
```

---

## Solución de Problemas

### Error: "Database connection refused"

```bash
# Verificar que PostgreSQL está corriendo
docker ps | grep postgres

# Si no está corriendo, iniciar
docker-compose up -d postgres
```

### Error: "Port already in use"

```bash
# Encontrar qué está usando el puerto
lsof -i :8080

# Matar el proceso
kill -9 <PID>

# O cambiar el puerto en application.yml
--server.port=8090
```

### Error: "JwtAuthenticationException"

```bash
# Verificar que JWT_SECRET es el mismo en todos los servicios
echo $JWT_SECRET

# O especificarlo explícitamente
-Djwt.secret="your-secret-key"
```

### Logs no se ven

```bash
# Aumentar nivel de log
--logging.level.root=DEBUG

# O en application.yml
logging:
  level:
    root: DEBUG
    com.udea.bancodigital: DEBUG
```

---

## Checklist de Despliegue

- [ ] Todos los servicios compilados exitosamente
- [ ] PostgreSQL corriendo con 3 bases de datos creadas
- [ ] Variables de entorno configuradas
- [ ] Docker compose levantado (`docker-compose up -d`)
- [ ] Health checks respondiendo OK en los 3 puertos
- [ ] Login funciona en Identity Service
- [ ] Core Banking API responde con token válido
- [ ] Audit Service registra eventos
- [ ] Métricas accesibles en `/actuator/prometheus`
- [ ] CORS configurado correctamente para frontend
- [ ] SSL/TLS configurado en producción
- [ ] Backups de base de datos configurados

---

## Próximos Pasos

1. **API Gateway**: Implementar Kong, Traefik, o AWS API Gateway
2. **Service Mesh**: Considerar Istio para trazabilidad distribuida
3. **Message Queue**: Implementar RabbitMQ o Kafka para eventos asincronos
4. **CI/CD**: Configurar GitHub Actions para despliegue automático
5. **Monitoring**: Setup de ELK Stack o Datadog
6. **Scaling**: Preparar Kubernetes para escalabilidad horizontal

