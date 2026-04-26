# Acoplamientos Remanentes con el Módulo Auth

Durante la preparación para extraer `identity-service`, se han mitigado la mayoría de los acoplamientos directos. Sin embargo, antes del split físico, persisten los siguientes puntos de contacto:

## 1. Filtro de Seguridad (SecurityConfig y JwtAuthenticationFilter)
El core de Spring Security ubicado en `src/main/java/com/udea/bancodigital/infrastructure/config/SecurityConfig.java` actualmente importa y depende directamente de `JwtAuthenticationFilter` que reside en el módulo `auth`. 
**Solución futura:** El core-banking y reporting deberán tener su propio filtro de validación de JWT (usando la clave pública o un secreto compartido) sin depender de la lógica de emisión ni de la blacklist de base de datos de `auth`.

## 2. Aprovisionamiento de Clientes
En el módulo `customers`, el caso de uso `CrearClienteUseCase` depende del puerto `ClienteAccessProvisioningPort`, cuya implementación concreta `ClienteAccessProvisioningAdapter` inyecta directamente un caso de uso de `auth`.
**Punto exacto de corte (Día 4):** El flujo de creación de cliente en `CrearClienteUseCase`. Actualmente la llamada a `accessProvisioningPort.provisionAccess()` es sincrónica. Cuando `identity-service` sea externo, este adaptador deberá cambiar para hacer una llamada HTTP (REST) o publicar un evento asíncrono (RabbitMQ/Kafka) para aprovisionar las credenciales.

## 3. Validación de Roles y Permisos (ClienteAccessControlAdapter)
El core sigue confiando en los roles inyectados en el `SecurityContext` por el filtro de JWT.
**Solución futura:** El JWT emitido por `identity-service` deberá contener los roles o claims necesarios para que los servicios downstream autoricen las peticiones de manera stateless.
