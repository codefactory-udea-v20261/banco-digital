## Descripción
<!-- Explica qué hace este PR y por qué es necesario -->

## Historia de Usuario vinculada
<!-- Closes #XX — Reemplaza XX con el número del issue en GitHub -->
- HU-XX: _descripción corta_

## Tipo de cambio
- [ ] Bug fix
- [ ] Nueva funcionalidad (HU)
- [ ] Refactor (sin cambio funcional)
- [ ] Documentación
- [ ] Infraestructura / DevOps
- [ ] Seguridad

## ¿Cómo se probó?
<!-- Describe los tests escritos y/o escenarios probados manualmente -->
- [ ] Pruebas unitarias escritas (patrón AAA)
- [ ] Pruebas ejecutadas localmente (`./mvnw test`)
- [ ] Probado manualmente con Postman (adjunta colección o screenshots)

## Quality Gate Checklist
- [ ] Cobertura ≥ 40% en el módulo afectado (JaCoCo)
- [ ] Sin errores de compilación ni warnings críticos
- [ ] Swagger/OpenAPI actualizado si hay cambios en endpoints
- [ ] No se exponen secretos ni credenciales en el código
- [ ] Complejidad ciclomática < 50 (revisado con SonarCloud)
- [ ] Deuda técnica < 2 días
- [ ] Código cumple los estándares de `docs/CODING_STANDARDS.md`

## Screenshots (si aplica)
<!-- Para endpoints, agrega captura de Swagger o Postman -->

## Revisores requeridos
<!-- Mínimo: Estefanía (garante arquitectónico) + 1 desarrollador adicional -->
- @egp1020 — Revisión arquitectónica obligatoria
- @[otro-reviewer]
