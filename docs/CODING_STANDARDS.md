# Guía de Estándares del Equipo — Banco Digital UdeA

> **Versión:** 1.0 | **Autor:** Estefanía Garcés (Arquitecta Líder) | **Revisión:** Carlos Vanegas
---

## 1 Nomenclatura de Ramas

| Tipo              | Patrón                         | Ejemplo                         |
|-------------------|--------------------------------|---------------------------------|
| Funcionalidad     | `feature/hu-XX-descripcion`    | `feature/hu-01-registro-cliente` |
| Bug fix           | `fix/descripcion-corta`        | `fix/validacion-saldo-negativo`  |
| Hotfix producción | `hotfix/descripcion`           | `hotfix/token-revocacion`       |
| Release           | `release/vX.Y.Z`               | `release/v1.0.0`                |
| Chore/docs        | `chore/descripcion`            | `chore/update-readme`           |

**Regla de oro:** NUNCA hacer push directo a `main` ni `develop`.
`main` = producción (Render). `develop` = integración del equipo.

## 2 Commits (Conventional Commits)

Formato: `<tipo>(<scope>): <descripción en imperativo>`

```
feat(customers): add POST /api/v1/clientes endpoint
fix(accounts): prevent negative balance on withdrawal
test(transactions): add unit tests for HU7 transfer
docs(adr): add ADR-001 clean architecture decision
chore(ci): add JaCoCo coverage threshold to pipeline
refactor(shared): extract trace ID generation to utility class
```

**Tipos válidos:** `feat`, `fix`, `test`, `docs`, `chore`, `refactor`, `perf`, `build`

## 3 Estructura de DTOs

```java
// CORRECTO — DTO en application/dto/ con validaciones
@Getter
@Builder
public class CrearClienteRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String primerNombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;
}

// INCORRECTO — Nunca exponer entidades JPA directamente
@GetMapping("/{id}")
public Cliente getCliente(@PathVariable UUID id) { ... } // ← Rompe Clean Architecture
```

## 4 Códigos de Estado HTTP

| Situación                    | Código | Descripción                       |
|------------------------------|--------|-----------------------------------|
| GET exitoso                  | 200    | OK                                |
| POST exitoso (creación)      | 201    | Created                           |
| PATCH/PUT exitoso            | 200    | OK                                |
| DELETE exitoso               | 204    | No Content                        |
| Datos inválidos (validación) | 400    | Bad Request                       |
| No autenticado               | 401    | Unauthorized                      |
| Sin permisos                 | 403    | Forbidden                         |
| Recurso no encontrado        | 404    | Not Found                         |
| Conflicto (duplicado)        | 409    | Conflict                          |
| Error interno                | 500    | Internal Server Error             |


## 5 Estándar de Respuesta API

Usar siempre `ApiResponse<T>` de `shared/web/`:

```java
// CORRECTO
return ResponseEntity.ok(ApiResponse.ok(clienteResponseDto));
return ResponseEntity.status(201).body(ApiResponse.created(cuentaDto));

// INCORRECTO
return ResponseEntity.ok(clienteResponseDto); // Rompe el contrato uniforme
```

## 6 Patrón de Pruebas Unitarias (AAA)

```java
@Test
@DisplayName("HU1 — Debe registrar cliente exitosamente cuando los datos son válidos")
void debeRegistrarCliente_cuandoDatosValidos() {
    // ── Arrange ──────────────────────────────────────────
    CrearClienteRequestDto request = CrearClienteRequestDto.builder()
        .primerNombre("María")
        .email("maria@test.com")
        .build();
    when(clienteRepository.existsByEmail(anyString())).thenReturn(false);

    // ── Act ───────────────────────────────────────────────
    ClienteResponseDto result = crearClienteUseCase.ejecutar(request);

    // ── Assert ────────────────────────────────────────────
    assertNotNull(result.getId());
    assertEquals("María", result.getPrimerNombre());
    verify(clienteRepository, times(1)).save(any());
}
```

## 7 Reglas de Revisión de Código (Code Review)

1. **Mínimo 2 aprobaciones** antes de fusionar (Estefanía obligatoria + 1 más).
2. **Quien codifica NO prueba manualmente** (QA cruzado).
3. **Prohibido merge** si el pipeline CI está rojo.
4. Las clases en `domain/` no pueden tener imports de Spring o JPA (verificar en PR).
5. Todo endpoint nuevo debe tener documentación Swagger (`@Operation`, `@ApiResponse`).

## 8 Variables de Entorno y Secretos

- **NUNCA** hardcodear credenciales en el código.
- Usar siempre `${VARIABLE:valor_por_defecto}` en `application.yml`.
- El archivo `.env` está en `.gitignore`. Usar `.env.example` como plantilla.
- Los secretos del CI se configuran en GitHub Secrets del repositorio.
