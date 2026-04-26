# Checklist para Creación de Nuevos Repositorios

Antes de abrir el repositorio de `identity-service` o `reporting-service`, asegúrate de cumplir con todos los pasos de esta lista para evitar acarrear deuda técnica o dependencias innecesarias del monolito.

## 1. Inicialización del Repositorio
- [ ] Crear el repositorio vacío en GitHub (`banco-digital-identity`, etc.).
- [ ] Configurar rama principal como `main`.
- [ ] Inicializar con el `README.md` específico del servicio.
- [ ] Añadir `.gitignore` estándar de Spring Boot/Java.

## 2. Configuración de Construcción y Empaquetado
- [ ] Copiar `pom.xml` o `build.gradle` (según aplique).
- [ ] Eliminar dependencias del `pom.xml` que NO se utilicen en este microservicio (ej. Flyway si es solo de lectura sin migrations, o dependencias de transacciones si no hace falta).
- [ ] Modificar `artifactId`, `name` y `description` en el POM.
- [ ] Copiar `.mvn` y `mvnw`.

## 3. Código Fuente
- [ ] Copiar el paquete específico del dominio (ej. `com.udea.bancodigital.auth`).
- [ ] Renombrar la clase `*Application.java` al nombre del servicio.
- [ ] Copiar SOLO las clases necesarias del paquete `shared/`.
- [ ] Copiar la configuración base (`infrastructure/config`) y eliminar los Beans que correspondan a otros dominios.
- [ ] Validar que NO existen importaciones `import com.udea.bancodigital.accounts.*`, `import com.udea.bancodigital.customers.*`, etc.

## 4. Base de Datos
- [ ] Copiar scripts de Flyway o equivalentes.
- [ ] Asegurar que los scripts solo creen tablas relevantes para este microservicio. Si la base de datos es compartida en esta fase inicial, extraer las migraciones necesarias para inicializar el entorno local.

## 5. Archivos de Propiedades (`application.yml`)
- [ ] Copiar `application.yml`, `application-local.yml`, `application-prod.yml`.
- [ ] Eliminar propiedades innecesarias (ej. configuraciones de colas, cron jobs o propiedades de seguridad que no apliquen).
- [ ] Cambiar puerto base (`server.port`).
  - Core: 8080
  - Identity: 8081
  - Reporting: 8082

## 6. Pruebas y DevOps
- [ ] Copiar y validar que las pruebas del módulo pasan (eliminar tests de otros módulos).
- [ ] Copiar y ajustar `Dockerfile`.
- [ ] Configurar GitHub Actions CI workflow (`.github/workflows/ci.yml`).

## 7. Verificación Final
- [ ] `./mvnw clean verify` ejecuta correctamente sin warnings de dependencias muertas.
- [ ] La aplicación levanta localmente y conecta a la BD.
- [ ] Swagger UI es accesible y muestra solo las APIs del servicio.
