# Banco Digital - Índice de Documentación

## 🚀 Inicio Rápido (5 minutos)

### 1. Desplegar localmente con Docker

```bash
cd /home/nia/projects/udea/arquisoft/banco-digital

# Compilar los 3 servicios
mvn clean package -DskipTests
cd core/banco-digital-identity && mvn clean package -DskipTests && cd ../..
cd core/banco-digital-audit && mvn clean package -DskipTests && cd ../..

# Ejecutar todo con Docker Compose
docker-compose up -d
```

### 2. Verificar que está levantado

```bash
# Health checks
curl http://localhost:8080/actuator/health   # Core Banking
curl http://localhost:8081/actuator/health   # Identity
curl http://localhost:8082/actuator/health   # Audit

# Acceder a Swagger UI
http://localhost:8080/swagger-ui.html
http://localhost:8081/swagger-ui.html
http://localhost:8082/swagger-ui.html
```

### 3. Hacer login y probar

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "cliente@banco.com",
    "clave": "Temp1234!",
    "mfaCode": "123456"
  }' | jq -r '.data.token')

# Usar el token
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/v1/clientes
```

---

## 📚 Documentación Completa

### Guías Principales

| Documento | Descripción | Tiempo |
|-----------|-------------|--------|
| **DEPLOYMENT-GUIDE.md** | Cómo desplegar en desarrollo, staging y producción | 20 min |
| **TESTING-GUIDE.md** | Cómo probar desde unit tests hasta load tests | 30 min |
| **MICROSERVICES-REPOS.md** | Estructura de los 3 microservicios | 5 min |

### Arquitectura Detallada

| Archivo | Contenido |
|---------|-----------|
| `docs/migration/WEEK-PLAN-3-MICROSERVICES.md` | Plan original de migración a microservicios |
| `docs/migration/REPO-SPLIT-MAP.md` | Mapa de qué módulos van dónde |
| `docs/migration/SERVICE-CONTRACTS-DRAFT.md` | Contratos de comunicación entre servicios |

---

## 🏗️ Arquitectura

### 3 Microservicios Independientes

```
┌─────────────────────────────────────────────────────┐
│                     PostgreSQL                      │
│  (3 bases de datos: core, identity, audit)         │
└──────────────┬──────────────┬──────────────────────┘
               │              │
      ┌────────▼────┐    ┌───▼─────────┐    ┌──────────────┐
      │   Core      │    │  Identity   │    │    Audit     │
      │  Banking    │    │  Service    │    │   Service    │
      │  (8080)     │    │  (8081)     │    │   (8082)     │
      │             │    │             │    │              │
      │ • Clientes  │    │ • Auth      │    │ • Events     │
      │ • Cuentas   │    │ • JWT       │    │ • Logs       │
      │ • Txs       │    │ • Users     │    │ • Auditoría  │
      └─────────────┘    └─────────────┘    └──────────────┘
```

### Estructura de Carpetas

```
banco-digital/
├── src/                           ← Core Banking Service
│   ├── accounts/
│   ├── customers/
│   ├── transactions/
│   └── ...
├── core/
│   ├── banco-digital-identity/    ← Identity Service
│   │   └── src/.../auth/
│   └── banco-digital-audit/       ← Audit Service
│       └── src/.../audit/
├── docker-compose.yml             ← Orquestación
└── Dockerfile                     ← Build Core Banking
```

---

## 🧪 Testing

### Niveles de Testing Disponibles

1. **Health Checks** (2 min) - Verificar servicios levantados
2. **Unit Tests** (5 min) - Pruebas unitarias de cada servicio
3. **Integration Tests** (10 min) - Integración entre módulos
4. **E2E Tests** (15 min) - Flujo completo de negocio
5. **Load Tests** (30 min) - Performance y estrés

Ver **TESTING-GUIDE.md** para detalles completos.

### Ejecutar E2E Test Completo

```bash
chmod +x scripts/e2e-test.sh
./scripts/e2e-test.sh

# Resultado esperado:
# ✅ ALL E2E TESTS PASSED
```

---

## 🚀 Despliegue

### Desarrollo (Docker Compose)

```bash
docker-compose up -d
```

### Servidor Local (sin Docker)

Ver **DEPLOYMENT-GUIDE.md → OPCIÓN 1**

Compilar servicios, crear BD, ejecutar:
```bash
java -jar target/banco-digital-core-banking-0.0.1-SNAPSHOT.jar
java -jar core/banco-digital-identity/target/banco-digital-identity-0.0.1-SNAPSHOT.jar
java -jar core/banco-digital-audit/target/banco-digital-audit-0.0.1-SNAPSHOT.jar
```

### Producción

Ver **DEPLOYMENT-GUIDE.md → OPCIÓN 2**

- Configurar variables de entorno (.env)
- Usar NGINX reverse proxy
- Certificados SSL/TLS
- Configurar CORS

---

## 📋 Endpoints Principales

### Identity Service (8081)

```
POST   /api/v1/auth/login                    → Login
POST   /api/v1/auth/logout                   → Logout
GET    /api/v1/auth/me                       → Datos de usuario
POST   /api/v1/internal/users/provision      → Provisionar acceso
```

### Core Banking Service (8080)

```
POST   /api/v1/clientes                      → Crear cliente
GET    /api/v1/clientes/{id}                 → Obtener cliente
PATCH  /api/v1/clientes/{id}                 → Actualizar cliente
POST   /api/v1/cuentas                       → Crear cuenta
GET    /api/v1/cuentas/{id}/saldo            → Consultar saldo
POST   /api/v1/transacciones                 → Transferencia
```

### Audit Service (8082)

```
GET    /api/v1/audit/logs                    → Obtener logs
POST   /api/v1/audit/events                  → Registrar evento
GET    /api/v1/audit/events/{id}             → Evento específico
```

---

## 🔧 Comandos Útiles

```bash
# Docker Compose
docker-compose up -d          # Iniciar todo
docker-compose down           # Parar todo
docker-compose logs -f        # Ver logs en vivo
docker-compose ps             # Ver estado

# Maven
mvn clean package -DskipTests # Compilar sin tests
mvn test                      # Ejecutar tests
mvn verify                    # Tests de integración

# PostgreSQL
psql -U postgres -l           # Listar BD
psql -U postgres -d banco_digital_core  # Conectar

# Health & Monitoring
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus
```

---

## 📊 Monitoreo

### Métricas de Prometheus

```
http://localhost:8080/actuator/prometheus
http://localhost:8081/actuator/prometheus
http://localhost:8082/actuator/prometheus
```

### Logs

```bash
docker-compose logs -f core-banking
docker-compose logs -f identity
docker-compose logs -f audit
```

### Health Check Endpoints

```
http://localhost:8080/actuator/health
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
```

---

## 🐛 Troubleshooting

### Servicio no levanta

```bash
# Verificar logs
docker-compose logs core-banking

# Verificar BD
docker-compose ps postgres
curl http://localhost:8080/actuator/health/db
```

### Error de puerto en uso

```bash
lsof -i :8080          # Encontrar proceso
kill -9 <PID>          # Matar proceso
```

### BD no conecta

```bash
# Verificar que PostgreSQL está corriendo
docker ps | grep postgres

# Reiniciar
docker-compose restart postgres
```

---

## 🎯 Próximos Pasos

1. **API Gateway** - Unificación de endpoints (Kong, Traefik)
2. **Service Mesh** - Observabilidad distribuida (Istio)
3. **Message Queue** - Eventos asincronos (RabbitMQ, Kafka)
4. **CI/CD** - Despliegue automático (GitHub Actions)
5. **Monitoring** - Dashboards y alertas (Prometheus + Grafana)
6. **Kubernetes** - Escalabilidad horizontal

Ver **DEPLOYMENT-GUIDE.md → Próximos Pasos** para más detalles.

---

## 📞 Soporte

- **Issues técnicos**: Ver logs con `docker-compose logs`
- **Arquitectura**: Leer `MICROSERVICES-REPOS.md`
- **Despliegue**: Leer `DEPLOYMENT-GUIDE.md`
- **Testing**: Leer `TESTING-GUIDE.md`

---

## ✅ Checklist de Verificación

Después de desplegar:

- [ ] `docker-compose ps` muestra 4 containers UP
- [ ] Health checks responden `{"status":"UP"}`
- [ ] Swagger UI accesible en 3 puertos
- [ ] Login funciona y retorna token
- [ ] Core Banking API responde con token válido
- [ ] Audit registra eventos
- [ ] Métricas accesibles en `/actuator/prometheus`

---

## 📝 Licencia

Banco Digital - UdeA CodeFactory 2026

---

**Última actualización**: 2026-04-14  
**Estado**: ✅ Producción Ready
