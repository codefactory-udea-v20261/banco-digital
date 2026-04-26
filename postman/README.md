# 📬 Postman Collections - Banco Digital Microservicios

Colecciones de Postman para probar la arquitectura de 3 microservicios de Banco Digital.

## 📋 Contenido

### Archivos principales

1. **banco-digital-microservices.postman_collection.json** (RECOMENDADO)
   - Colección completa actualizada para microservicios
   - 30+ requests organizadas en 5 secciones
   - Incluye health checks, autenticación, CRUD, y flujos E2E
   - Versión actual: v2.0 (Abril 2026)

2. **banco-digital-local.postman_environment.json** (RECOMENDADO)
   - Environment con variables preconfiguradas
   - URLs de los 3 servicios (8080, 8081, 8082)
   - Placeholders para tokens y IDs que se auto-capturan
   - Credenciales de prueba

3. **banco-digital-api.postman_collection.json** (LEGACY)
   - Colección anterior para referencia
   - Mantiene compatibilidad con versión monolítica
   - No se recomienda para nueva arquitectura

## 🚀 Instalación

### Opción 1: Import directo (Recomendado)

```bash
# 1. Abre Postman
# 2. Click en "Import" (esquina superior izquierda)
# 3. Selecciona estos 2 archivos (Ctrl+click):
#    - banco-digital-microservices.postman_collection.json
#    - banco-digital-local.postman_environment.json
# 4. Click "Import"
# 5. Selecciona "Banco Digital - Local" en el dropdown de environments
```

### Opción 2: Copiar archivos a Postman

```bash
# En Linux/Mac:
cp postman/banco-digital-*.postman_collection.json ~/Postman/collections/
cp postman/banco-digital-*.postman_environment.json ~/Postman/environments/

# En Windows:
copy postman\banco-digital-*.postman_collection.json %APPDATA%\Postman\collections
copy postman\banco-digital-*.postman_environment.json %APPDATA%\Postman\environments
```

## 📊 Estructura de la Colección

```
banco-digital-microservices
├── 1. SETUP - Variables y Health Checks
│   ├── Health Check - Core Banking (8080)
│   ├── Health Check - Identity Service (8081)
│   └── Health Check - Audit Service (8082)
│
├── 2. IDENTITY SERVICE (8081) - Autenticación
│   ├── Auth - Login
│   ├── Auth - Logout
│   ├── Auth - Get Current User
│   ├── Internal - Provision Client Access ⭐ NUEVO
│   └── Internal - Check Email Exists ⭐ NUEVO
│
├── 3. CORE BANKING (8080) - Gestión Financiera
│   ├── Clientes
│   │   ├── Crear Cliente
│   │   ├── Obtener Cliente
│   │   └── Actualizar Cliente
│   ├── Cuentas
│   │   ├── Crear Cuenta
│   │   └── Consultar Saldo
│   └── Transacciones
│       └── Crear Transacción
│
├── 4. AUDIT SERVICE (8082) - Auditoría
│   ├── Audit - Get Logs
│   ├── Audit - Record Event
│   └── Audit - Get Event by ID
│
└── 5. TESTING - Flujo Completo
    └── Flujo 1: Login → Crear Cliente → Crear Cuenta
```

## 🧪 Flujos de Testing

### Flujo A: Verificación Rápida (5 min)

```
1. Selecciona carpeta: "1. SETUP - Variables y Health Checks"
2. Click derecho → "Run collection"
3. Verifica que los 3 servicios respondan "UP"
```

**Resultado esperado:** 3 health checks exitosos ✅

### Flujo B: Testing Completo (15 min)

```
1. Ejecuta: 2. IDENTITY SERVICE - Auth - Login
   → Captura jwtToken automáticamente
   
2. Ejecuta: 3. CORE BANKING - Clientes - Crear Cliente
   → Captura clienteId automáticamente
   
3. Ejecuta: 3. CORE BANKING - Cuentas - Crear Cuenta
   → Captura cuentaId automáticamente
   
4. Ejecuta: 4. AUDIT SERVICE - Record Event
   → Registra evento de auditoría
   
5. Ejecuta: 4. AUDIT SERVICE - Get Logs
   → Verifica que el evento fue registrado
```

**Resultado esperado:** Flujo completo sin errores ✅

### Flujo C: E2E Automático (20 min)

```
1. Abre carpeta: "5. TESTING - Flujo Completo"
2. Click derecho → "Run collection"
3. Postman ejecuta todas las requests en orden
4. Observa cómo se capturan y usan variables automáticamente
```

**Resultado esperado:** Todas las requests ejecutadas exitosamente ✅

## 🔑 Variables de Environment

| Variable | Descripción | Auto-capturada |
|----------|-------------|---|
| `baseUrl` | http://localhost:8080 | No |
| `identityServiceUrl` | http://localhost:8081 | No |
| `auditServiceUrl` | http://localhost:8082 | No |
| `jwtToken` | Token JWT de autenticación | Sí (desde Login) |
| `clienteId` | ID del cliente creado | Sí (desde Crear Cliente) |
| `cuentaId` | ID de la cuenta creada | Sí (desde Crear Cuenta) |
| `asesorCorreo` | Credencial de prueba | No |
| `asesorClave` | Credencial de prueba | No |

## 🔐 Autenticación

### Login

```
POST http://localhost:8081/api/v1/auth/login

{
  "correo": "cliente@banco.com",
  "clave": "Temp1234!",
  "mfaCode": "123456"
}

Response:
{
  "status": "success",
  "data": "eyJhbGc..."  // JWT Token
}
```

### Uso en requests subsecuentes

```
Header: Authorization: Bearer {{jwtToken}}
```

El token se captura automáticamente y está disponible en todas las requests.

## 🆕 Nuevos Endpoints (v2.0)

### Identity Service - Internal APIs

#### Provision Client Access
```
POST http://localhost:8081/api/v1/internal/users/provision

{
  "clienteId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "newclient@banco.com"
}
```

Se ejecuta automáticamente cuando Core Banking crea un nuevo cliente.

#### Check Email Exists
```
GET http://localhost:8081/api/v1/internal/users/exists?email=test@banco.com

Response:
{
  "exists": true|false
}
```

Verifica si un email ya tiene usuario antes de crear uno nuevo.

## 🧩 Casos de Uso

### 1. Cliente nuevo se registra
```
1. Login (obtener token)
2. Core Banking: Crear Cliente
3. Identity Service: Provision Client Access (automático)
4. Audit Service: Record Event
5. Core Banking: Crear Cuenta
```

### 2. Verificar seguridad
```
1. Health Checks (todos los servicios)
2. Login con credenciales inválidas
3. Acceder a endpoint sin token
4. Intentar acceder a recurso sin permisos
```

### 3. Auditoría y trazabilidad
```
1. Realizar transacción
2. Audit Service: Record Event
3. Audit Service: Get Logs
4. Audit Service: Get Event by ID
```

## 🐛 Troubleshooting

### Error: "Cannot POST /api/v1/auth/login"
- **Solución:** Verifica que Identity Service esté corriendo en puerto 8081
- Ejecuta: `docker-compose ps`

### Error: "401 Unauthorized"
- **Solución:** El token JWT expiró. Ejecuta Login nuevamente
- El token se auto-captura en variable `{{jwtToken}}`

### Error: "Connection refused"
- **Solución:** Verifica que los servicios estén levantados
- Ejecuta: `docker-compose up -d`

### Variables vacías ({{clienteId}}, {{cuentaId}})
- **Solución:** Ejecuta primero Crear Cliente y Crear Cuenta
- Las variables se auto-capturan de las respuestas

### Response vacío o error 500
- **Solución:** Verifica los logs del servicio
- Ejecuta: `docker-compose logs core-banking`

## 📖 Documentación Relacionada

- [INDEX.md](../INDEX.md) - Guía general del proyecto
- [DEPLOYMENT-GUIDE.md](../DEPLOYMENT-GUIDE.md) - Cómo desplegar
- [TESTING-GUIDE.md](../TESTING-GUIDE.md) - Guía completa de testing
- [QUICK-START.md](../QUICK-START.md) - Inicio rápido

## 💡 Tips

1. **Auto-run tests:** Postman ejecuta los scripts de "test" después de cada request
2. **Variables globales:** Se capturan automáticamente de responses con `pm.environment.set()`
3. **Pre-request scripts:** Disponibles para setup antes de cada request
4. **Collection runner:** Ejecuta múltiples requests en orden con parámetros

## 🔄 Versionado

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 2.0 | Abril 2026 | Arquitectura microservicios, Identity Service interna |
| 1.0 | Enero 2026 | Colección original monolítica |

## ✨ Próximos pasos

- [ ] Añadir más casos de test
- [ ] Implementar scripting avanzado
- [ ] Crear flows condicionales
- [ ] Integrar con CI/CD (Newman)

---

**Última actualización:** Abril 14, 2026
**Mantenedor:** Banco Digital Team
