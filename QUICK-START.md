# 🚀 Quick Start - Banco Digital Microservicios

## Inicio en 5 Minutos

### 1️⃣ Compilar (2 min)

```bash
cd /home/nia/projects/udea/arquisoft/banco-digital

# Compilar Core Banking
mvn clean package -DskipTests

# Compilar Identity
cd core/banco-digital-identity && mvn clean package -DskipTests && cd ../..

# Compilar Audit  
cd core/banco-digital-audit && mvn clean package -DskipTests && cd ../..
```

### 2️⃣ Ejecutar (1 min)

```bash
# Volver a raíz y ejecutar
cd /home/nia/projects/udea/arquisoft/banco-digital
docker-compose up -d
```

### 3️⃣ Verificar (1 min)

```bash
# Todos deben responder UP
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

### 4️⃣ Probar (1 min)

**Opción A: Swagger UI (Visual)**
- Core Banking: http://localhost:8080/swagger-ui.html
- Identity: http://localhost:8081/swagger-ui.html
- Audit: http://localhost:8082/swagger-ui.html

**Opción B: Curl Login**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "cliente@banco.com",
    "clave": "Temp1234!",
    "mfaCode": "123456"
  }'
```

---

## 📋 Pruebas Rápidas

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas
curl http://localhost:8080/actuator/prometheus | head -20

# Ver logs
docker-compose logs -f

# Detener todo
docker-compose down
```

---

## 📚 Documentación

| Documento | Propósito | Tiempo |
|-----------|-----------|--------|
| **INDEX.md** | Guía completa | 10 min |
| **DEPLOYMENT-GUIDE.md** | Despliegue | 20 min |
| **TESTING-GUIDE.md** | Testing | 30 min |

---

## ⚡ Comandos de Rescate

```bash
# Ver estado
docker-compose ps

# Reiniciar un servicio
docker-compose restart core-banking

# Logs en vivo
docker-compose logs -f core-banking

# Conectar a BD
psql -U postgres -d banco_digital_core
```

---

## ✅ Verificación Final

Si ves esto = está funcionando ✅

```
✓ docker-compose ps → 4 containers UP
✓ curl localhost:8080/actuator/health → UP
✓ http://localhost:8080/swagger-ui.html → Accesible
✓ Login retorna token
```

---

**¡Listo!** Ahora lee INDEX.md para más detalles.
