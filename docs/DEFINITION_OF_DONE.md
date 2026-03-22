# Definition of Done (DoD) — Banco Digital UdeA

> Una Historia de Usuario (HU) se considera **DONE** cuando cumple TODOS estos criterios:

## Criterios Técnicos

- [ ] El código compila sin errores ni warnings críticos
- [ ] Pruebas unitarias escritas con patrón AAA (Arrange/Act/Assert)
- [ ] Cobertura de pruebas ≥ 60% en la capa de servicios del módulo
- [ ] El pipeline CI en GitHub Actions está en verde (build + tests pasando)
- [ ] Sin vulnerabilidades críticas reportadas por SonarCloud
- [ ] Deuda técnica < 2 días (SonarCloud Quality Gate)
- [ ] Complejidad ciclomática < 50 por clase

## Criterios Arquitectónicos

- [ ] Se respeta la Regla de Dependencia (domain sin imports de framework)
- [ ] Los DTOs están en `application/dto/`, nunca se exponen entidades JPA
- [ ] Toda operación de mutación financiera usa `@Transactional`
- [ ] Respuestas envueltas en `ApiResponse<T>`
- [ ] Excepciones de negocio heredan de `BusinessException`

## Criterios de API / Documentación

- [ ] Endpoint documentado en Swagger con `@Operation` y ejemplos
- [ ] Códigos HTTP correctos según estándar del equipo
- [ ] Colección Postman actualizada por el desarrollador
- [ ] Archivo Swagger actualizado y entregado a QA

## Criterios de Proceso

- [ ] Pull Request revisado y aprobado por Estefanía + 1 adicional
- [ ] Tarea movida a "Done" en Azure DevOps
- [ ] Horas registradas en Azure DevOps
- [ ] Criterios de aceptación técnicos entregados a equipo QA (Mariana/Alejandro)
