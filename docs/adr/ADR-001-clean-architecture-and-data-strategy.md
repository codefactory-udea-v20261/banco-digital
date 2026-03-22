# ADR-001: Clean Architecture como Estilo Arquitectónico y Estrategia de Datos

| Campo        | Valor                                     |
|--------------|-------------------------------------------|
| **Estado**   | Aceptado                                  |
| **Fecha**    | 2026-03-21                                |
| **Autores**  | Estefanía Garcés (Arquitecta Líder), equipo |
| **Contexto** | Sprint 0 — Banco Digital UdeA CodeFactory |

---

## 1 Contexto

Necesitamos definir la estructura fundamental del backend de un banco digital antes de escribir
la primera línea de lógica de negocio. Las decisiones tomadas aquí afectan la testabilidad,
mantenibilidad y alineación con los criterios de evaluación del proyecto.

**Pregunta clave:** ¿Cómo estructuramos el código para que la lógica de negocio financiero
sea independiente del framework, la base de datos y cualquier mecanismo de entrega?

## 2 Decisión

### 2.1 Estilo Arquitectónico: Clean Architecture + Monolito Modular

Adoptamos Clean Architecture (Robert C. Martin) organizada como un Monolito Modular
con los siguientes módulos de negocio:

```
com.udea.bancodigital/
├── customers/        # Gestión de clientes
├── accounts/         # Cuentas financieras
├── transactions/     # Transacciones (OLTP)
├── auth/             # Autenticación y autorización
└── audit/            # Auditoría transversal
```

Cada módulo respeta estrictamente la Regla de Dependencia: las capas internas nunca
conocen las externas.

```
[Infrastructure] → [Application] → [Domain]
     ↑ (inyección de dependencias vía Spring)
```

### 2.2 Capas por módulo

| Capa               | Paquete                    | Responsabilidad                              |
|--------------------|----------------------------|----------------------------------------------|
| `domain/`          | `model/`, `port/`, `exception/` | Entidades puras, interfaces de puertos, excepciones de negocio |
| `application/`     | `usecase/`, `dto/`         | Casos de uso, orquestación, DTOs de entrada/salida |
| `infrastructure/`  | `adapter/`, `config/`, `entity/` | Controladores REST, repositorios JPA, configuraciones Spring |
| `shared/`          | `web/`, `exception/`, `util/` | Código transversal: ApiResponse, GlobalExceptionHandler |

### 2.3 Estrategia de Datos: Separación OLTP / OLAP

**Regla Fundamental:**

> La lógica transaccional financiera (OLTP) vive 100% en la capa de aplicación Java
> (`@Transactional`), garantizando rollback automático y testabilidad.
>
> Los procedimientos almacenados (SP) se reservan **EXCLUSIVAMENTE** para operaciones
> analíticas y de reporte (OLAP), donde las agregaciones masivas en el motor de BD
> son más eficientes que traer datos a Java.

**Aplicación práctica:**

- `HU7 Transferencia de dinero` → `@Transactional` en Java (Application Layer)
- `HU8 Retiro de dinero` → `@Transactional` en Java (Application Layer)
- `HU10 Reporte de actividad` → Stored Procedure en PostgreSQL (OLAP)
- Triggers de auditoría → Base de datos (garantía absoluta, no evadible desde Java)

## 3 Alternativas Consideradas

| Opción                   | Pros                              | Contras                                    |
|--------------------------|-----------------------------------|--------------------------------------------|
| Arquitectura en capas clásica | Simple, familiar                 | Fugas de lógica al controlador, difícil de testear |
| Hexagonal pura           | Máxima separación                 | Curva de aprendizaje alta para un equipo mixto |
| **Clean Architecture**  | Testable, clara, alineada con criterios | Requiere disciplina en imports             |
| Microservicios           | Escalable                         | Overhead operacional excesivo para el alcance académico |

## 4 Consecuencias

### Positivas
- La lógica de negocio financiero es 100% testeable con mocks (sin Spring, sin BD).
- El cambio de PostgreSQL a otro motor solo afecta `infrastructure/`, nunca `domain/`.
- La separación OLTP/OLAP cumple explícitamente el criterio de "procedimientos almacenados para operaciones analíticas" requerido por los criterios del proyecto.
- El `GlobalExceptionHandler` garantiza respuestas uniformes `ApiResponse<T>` en todos los endpoints.

### Negativas / Trade-offs
- Mayor número de clases que en una arquitectura en capas clásica.
- Los desarrolladores deben respetar conscientemente la Regla de Dependencia (revisada en Code Review por Estefanía).

## 5 Estándares de Implementación Derivados de esta Decisión

1. Ninguna clase en `domain/` puede importar clases de Spring, JPA, Lombok ni ningún framework.
2. Los DTOs viven en `application/dto/`, nunca se expone una entidad JPA directamente al controlador.
3. Los mapeos entity↔domain↔dto se realizan exclusivamente con MapStruct.
4. Toda operación financiera de mutación de estado requiere `@Transactional` en el caso de uso.

## 6 Referencias

- Robert C. Martin, *Clean Architecture: A Craftsman's Guide* (2017)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- Criterios_GeneracionProyecto_Avanzado — CodeFactory UdeA (2026)
