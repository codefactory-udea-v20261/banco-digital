# Banco Digital Microservices Architecture

## 📁 Estructura de Repositorios

El proyecto `banco-digital` contiene el Core Banking Service en la raíz, y dos microservicios adicionales organizados en la carpeta `core/`:

```
/home/nia/projects/udea/arquisoft/banco-digital/          ← CORE BANKING SERVICE (Main)
│
├── src/main/java/com/udea/bancodigital/
│   ├── accounts/          ← Módulo de Cuentas
│   ├── customers/         ← Módulo de Clientes
│   ├── transactions/      ← Módulo de Transacciones
│   ├── infrastructure/
│   └── shared/
│
├── core/
│   ├── banco-digital-identity/                           ← IDENTITY SERVICE (Repo)
│   │   ├── src/main/java/com/udea/bancodigital/auth/
│   │   ├── pom.xml
│   │   └── [config local del servicio]
│   │
│   └── banco-digital-reporting/                          ← REPORTING SERVICE (Repo)
│       ├── src/main/java/com/udea/bancodigital/reporting/
│       ├── pom.xml
│       └── [config local del servicio]
```

## 🚀 Servicios Disponibles

| Servicio | Puerto | Ubicación | Rama Git | Estado |
|----------|--------|-----------|----------|--------|
| **Core Banking** | 8080 | `./` (raíz) | `main` | ✅ Activo |
| **Identity** | 8081 | `./core/banco-digital-identity/` | `main` | ✅ Activo |
| **Reporting** | 8082 | `./core/banco-digital-reporting/` | `main` | ✅ Activo |

## 🏗️ Compilación

### Compilar Core Banking (desde raíz)
```bash
cd /home/nia/projects/udea/arquisoft/banco-digital
mvn clean verify
```

### Compilar Identity Service
```bash
cd core/banco-digital-identity
mvn clean verify
```

### Compilar Reporting Service
```bash
cd core/banco-digital-reporting
mvn clean verify
```

### Compilar todos los servicios (script)
```bash
for service in . core/banco-digital-identity core/banco-digital-reporting; do
  (cd "$service" && echo "Building $service..." && mvn clean verify -q)
done
```

## 🔗 Sincronización con GitHub

Los tres servicios tienen repositorios independientes en GitHub:

### Core Banking (Este directorio)
```bash
git remote -v
# origin    https://github.com/codefactory-udea-v20261/banco-digital.git
```

### Identity Service
```bash
cd core/banco-digital-identity
git remote -v
# origin    https://github.com/codefactory-udea-v20261/banco-digital-identity.git
```

### Reporting Service
```bash
cd core/banco-digital-reporting
git remote -v
# origin    https://github.com/codefactory-udea-v20261/banco-digital-reporting.git
```

## 📋 Estado Actual (2026-04-12)

✅ **Completado:**
- Creación de tres repositorios GitHub
- Migración de código de dominio
- Configuración de puertos por servicio
- Compilación exitosa de todos los servicios
- Tests pasando en todos los módulos
- Commits pusheados a GitHub

⏳ **Próximos pasos:**
- [ ] Implementar comunicación inter-servicios (REST APIs)
- [ ] Configurar JWT secret sharing
- [ ] Setup docker-compose para local
- [ ] CI/CD en GitHub Actions
- [ ] Tests de integración end-to-end

## 📚 Referencias

- Plan de Trabajo: `docs/migration/WEEK-PLAN-4-REPOSITORIES.md`
- Mapa de Carpetas: `docs/migration/REPO-SPLIT-MAP.md`
- Checklist: `docs/migration/REPO-CREATION-CHECKLIST.md`

## 🔧 Configuración por Servicio

### Variables de Entorno (Local)
```bash
# Todos necesitan estos valores (pueden ser iguales en local)
JWT_SECRET=tu-secreto-aqui
JWT_EXPIRATION=86400

# Identidad
IDENTITY_SERVICE_URL=http://localhost:8081
IDENTITY_PORT=8081

# Reporting
REPORTING_SERVICE_URL=http://localhost:8082
REPORTING_PORT=8082

# Core Banking
CORE_BANKING_URL=http://localhost:8080
CORE_BANKING_PORT=8080
```

### Puerto Base
```yaml
# Core Banking: application.yml
server:
  port: 8080

# Identity: core/banco-digital-identity/src/main/resources/application.yml
server:
  port: 8081

# Reporting: core/banco-digital-reporting/src/main/resources/application.yml
server:
  port: 8082
```

## 💡 Notas Importantes

1. **Estructura Git**: Cada directorio es un repositorio independiente con su propio `.git/` y origin
2. **Sincronización**: Los cambios en cada servicio se pushean a su respectivo repo en GitHub
3. **Dependencias Compartidas**: `shared/` se copió en cada servicio para independencia
4. **Base de Datos**: Actualmente compartida, plan de segregación en próxima fase
5. **JWT**: Los tres servicios usan el mismo secreto para validación de tokens

## 🚀 Próxima Fase

Para la siguiente sesión implementar:
1. HTTP client en Core Banking para llamar Identity Service
2. Endpoint `/api/v1/internal/users/provision-client-access` en Identity
3. docker-compose.yml maestro

---

**Última actualización**: 2026-04-12  
**Estado**: Repositorios creados, código migrado, compilando correctamente
