# 🔐 Testing Credentials - Banco Digital Microservices

## Seed Data Overview

Las credenciales de prueba se cargan automáticamente cuando inicia el Docker stack mediante el script:
- **Location**: `docker-entrypoint-initdb.d/01-seed-test-data.sql`
- **Execution**: Se ejecuta automáticamente al iniciar PostgreSQL
- **Affected Databases**: 
  - `banco_digital_core` - Cliente de prueba
  - `banco_digital_identity` - Usuario de prueba

---

## Test User Credentials

### 🔑 Primary Test User: Cliente Banco Digital

| Property | Value |
|----------|-------|
| **Email** | `cliente@banco.com` |
| **Password** | `Temp1234!` |
| **Username** | `cliente@banco.com` (same as email) |
| **Role** | `CLIENTE` |
| **MFA Active** | `false` (MFA not required) |
| **Account Status** | `ACTIVE` |
| **Failed Attempts** | `0` |

### 📋 Associated Client Data

| Property | Value |
|----------|-------|
| **Cédula** | `1234567890` |
| **Name** | Juan Carlos Pérez García |
| **Email** | `cliente@banco.com` |
| **Phone** | `3101234567` |
| **Birth Date** | `1990-05-15` |
| **Status** | `ACTIVE` |

### 🏦 Associated Bank Account

| Property | Value |
|----------|-------|
| **Account Number** | `1000000001` |
| **Account Type** | `AHORROS` (Savings) |
| **Initial Balance** | `1,000,000.00 COP` |
| **Status** | `ACTIVA` |
| **Owner** | Juan Carlos Pérez García |

---

## Authentication Flow

### 1. Login Request

```bash
curl --location 'http://localhost:8081/api/v1/auth/login' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "correo": "cliente@banco.com",
    "clave": "Temp1234!",
    "mfaCode": "123456"
  }'
```

### 2. Expected Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOiIwYmEyM2I3NC1iODZiLTQwOWYtOWNmOS1kOWU0MTBhMjZkNzUiLCJtZmFBY3Rpdm8iOmZhbHNlLCJyb2xlcyI6WyJDTElFTlRFIl0sImJsb3F1ZWFkbyI6ZmFsc2UsImFjdGl2byI6dHJ1ZSwic3ViIjoiY2xpZW50ZUBiYW5jby5jb20iLCJqdGkiOiIxNWY4ZWNmZi0zODMxLTQ3YjUtOGRiOS01MDk2MzZlZjY4MDMiLCJpYXQiOjE3NzYxODgwMTIsImV4cCI6MTc3NjE5MTYxMn0.t6L1c_-bdXV8KTYn-_YPLuCp91YyEzLHCxDMoZfAZlA"
}
```

### 3. Using Token in Requests

Add the token to subsequent requests:

```bash
curl --location 'http://localhost:8080/api/v1/clientes' \
  --header 'Authorization: Bearer <TOKEN_FROM_LOGIN>' \
  --header 'Content-Type: application/json'
```

---

## Database Verification

### Check Seed Data in Core Banking

```bash
# Connect to container
docker exec -it banco-digital-db psql -U postgres -d banco_digital_core

# List all clients
SELECT id, numero_cedula, primer_nombre, email FROM cliente;

# List all accounts
SELECT id, numero_cuenta, tipo_cuenta_id, saldo FROM cuenta;
```

### Check Seed Data in Identity Service

```bash
# Connect to container
docker exec -it banco-digital-db psql -U postgres -d banco_digital_identity

# List all users
SELECT id, username, activo, mfa_activo FROM usuario;

# List all roles
SELECT id, nombre FROM rol;

# List user-role associations
SELECT ur.usuario_id, ur.rol_id, r.nombre 
FROM usuario_rol ur
JOIN rol r ON ur.rol_id = r.id;
```

---

## Password Hash Information

### Current Password Hash

- **Algorithm**: BCrypt
- **Rounds (Strength)**: 12
- **Password**: `Temp1234!`
- **Hash**: `$2b$12$9m1/h7yIdn4zIWiVgzf4veGZ3TVbrE1FBNrI8IJ.Ryq5Na9jbEclW`

### Generate New Password Hashes

If you need to generate a new password hash for testing:

#### Using Python

```bash
python3 << 'EOF'
import bcrypt

password = "YourNewPassword123!"
hash_bytes = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt(rounds=12))
hash_str = hash_bytes.decode('utf-8')
print(f"Hash: {hash_str}")
EOF
```

#### Using Java (Spring Boot)

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hash = encoder.encode("YourNewPassword123!");
System.out.println(hash);
```

#### Update Hash in Database

```bash
docker exec -it banco-digital-db psql -U postgres -d banco_digital_identity -c \
  "UPDATE usuario SET password_hash = '\$2b\$12\$...' WHERE username = 'cliente@banco.com';"
```

---

## Creating Additional Test Users

### 1. Create Cliente in Core Banking

```bash
INSERT INTO cliente (
  numero_cedula, 
  primer_nombre, 
  segundo_nombre,
  primer_apellido, 
  segundo_apellido, 
  email, 
  telefono, 
  fecha_nacimiento, 
  activo
) VALUES (
  '9876543210',
  'María',
  NULL,
  'González',
  'López',
  'maria@banco.com',
  '3109876543',
  '1995-08-20',
  true
);
```

### 2. Create Usuario in Identity Service

```bash
-- Generate hash first using Python/Java method above
INSERT INTO usuario (
  username,
  password_hash,
  activo,
  mfa_activo,
  intentos_fallidos,
  created_at
) VALUES (
  'maria@banco.com',
  '$2b$12$<HASH_HERE>',
  true,
  false,
  0,
  NOW()
);

-- Assign role
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuario u
CROSS JOIN rol r
WHERE u.username = 'maria@banco.com'
  AND r.nombre = 'CLIENTE';
```

---

## Account Lockout Rules

Users are locked after 3 failed login attempts:
- **Max Failed Attempts**: 3
- **Lockout Action**: Account gets blocked
- **Reset**: Automatic on successful login

### Checking Locked Accounts

```bash
docker exec -it banco-digital-db psql -U postgres -d banco_digital_identity -c \
  "SELECT id, username, bloqueado_hasta, intentos_fallidos FROM usuario WHERE username = 'cliente@banco.com';"
```

### Unlocking an Account

```bash
docker exec -it banco-digital-db psql -U postgres -d banco_digital_identity -c \
  "UPDATE usuario SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE username = 'cliente@banco.com';"
```

---

## API Testing with Postman

### Import Collections

1. Open Postman
2. Click **Import** in the top-left
3. Select these files:
   - `postman/banco-digital-microservices.postman_collection.json`
   - `postman/banco-digital-local.postman_environment.json`

### Set Environment

1. Select environment: **Banco Digital - Local**
2. The JWT token will auto-capture from login requests

### Quick Test (5 minutes)

1. **Health Checks** folder → Run all 3 health check requests
2. Verify all services return `"status":"UP"`

### Full Test (15 minutes)

1. **Identity Service** → Login → Extract token
2. **Core Banking** → Create Cliente → Extract ID
3. **Core Banking** → Create Cuenta
4. **Core Banking** → Query Saldo
5. **Audit Service** → Record Event

---

## Troubleshooting

### ❌ "Credenciales inválidas" (Invalid Credentials)

**Cause**: Password hash mismatch
- **Solution**: Regenerate hash using Python/Java, update database
- **Check**: Verify password_hash in `usuario` table matches algorithm

### ❌ "Cuenta Bloqueada" (Locked Account)

**Cause**: 3+ failed login attempts
- **Solution**: Reset failed attempts count in database
- **Command**: 
  ```bash
  docker exec -it banco-digital-db psql -U postgres -d banco_digital_identity -c \
    "UPDATE usuario SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE username = 'cliente@banco.com';"
  ```

### ❌ "No user found" Error

**Cause**: Seed data didn't load
- **Check**: 
  ```bash
  docker exec -it banco-digital-db psql -U postgres -d banco_digital_identity -c \
    "SELECT * FROM usuario WHERE username = 'cliente@banco.com';"
  ```
- **Solution**: Manually run seed script (see Database Verification section)

### ❌ Account Not Found in Core Banking

**Cause**: Cliente not linked to usuario
- **Note**: `cliente_id` in `usuario` table can be NULL initially
- **For production**: Implement proper account provisioning flow

---

## Best Practices for Testing

✅ **DO:**
- Use test credentials only in development/staging environments
- Regenerate hashes for production deployments
- Rotate test users regularly
- Document custom test users you create
- Use environment files in Postman for different contexts

❌ **DON'T:**
- Commit real password hashes to version control
- Use hardcoded credentials in test scripts
- Share seed credentials in Slack/email
- Use production credentials for testing
- Leave account lockouts unresolved

---

## Migration to Production

Before deploying to production:

1. **Update password hashes**: Generate new hashes for production users
2. **Disable test accounts**: Set `activo = false` for test users
3. **Configure email verification**: Implement /exists endpoint properly
4. **Enable MFA**: Set `mfa_activo = true` for admin accounts
5. **Audit logging**: Enable and monitor all login attempts
6. **Rate limiting**: Implement API rate limiting
7. **JWT secret rotation**: Change hardcoded dev secret

---

## Related Documentation

- 📖 [TESTING-GUIDE.md](./TESTING-GUIDE.md) - Full testing guide
- 📖 [DEPLOYMENT-GUIDE.md](./DEPLOYMENT-GUIDE.md) - Deployment instructions
- 📖 [postman/README.md](./postman/README.md) - Postman collections guide
- 📖 [INDEX.md](./docs/INDEX.md) - Complete documentation index
