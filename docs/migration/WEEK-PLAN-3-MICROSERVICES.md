# Plan de Trabajo de 1 Semana

Este plan toma como base la arquitectura objetivo ya definida para:

- `core-banking-service`
- `identity-service`
- `reporting-service`

La idea no es extraer todo en una sola pasada. En una semana lo realista es dejar listos los contratos, mover primero las responsabilidades mas claras y reducir el acoplamiento del monolito actual para que el split a repos separados no sea traumático.

## Objetivo de la semana

Cerrar la semana con estos resultados:

1. Una base estable del monolito actual con limites mas claros.
2. Contratos iniciales definidos para `identity` y `reporting`.
3. Una primera version funcional de `reporting` separada a nivel de modulo.
4. La ruta de extraccion de `identity` lista para ejecutarse.
5. Criterios claros para abrir los repos nuevos.

## Alcance de esta semana

### Si entra en la semana

- Seguir endureciendo limites entre modulos.
- Terminar de separar la lectura analitica en `reporting`.
- Desacoplar el core de detalles internos de seguridad.
- Definir APIs y payloads minimos para servicios externos.
- Preparar backlog tecnico para abrir repos nuevos.

### No entra en la semana

- Desplegar los tres servicios por separado en produccion.
- Montar observabilidad distribuida completa.
- Resolver toda la comunicacion asincrona del sistema.
- Separar `transactions` del core bancario.

## Plan por dias

### Dia 1

- Revisar y consolidar el corte funcional de los tres servicios.
- Confirmar que `reporting` se mantiene como servicio de lectura.
- Confirmar que `identity` sera el primer servicio en salir.
- Dejar documentados los contratos preliminares.

Entregables:

- ADR validado.
- Plan semanal validado.
- Contratos iniciales escritos.

### Dia 2

- Terminar de sacar del core cualquier dependencia directa de JWT.
- Identificar todos los puntos donde `customers` o `accounts` dependen de `auth`.
- Reemplazar dependencias concretas por puertos o proveedores compartidos.

Entregables:

- Lista de acoplamientos remanentes con `auth`.
- Refactor tecnico en la rama para reducir dependencias directas.

### Dia 3

- Completar el modulo `reporting` dentro del monolito.
- Mover endpoints de consulta analitica hacia `/api/v1/reportes`.
- Definir si `reporting` leera por funciones SQL, consultas dedicadas o vistas.

Entregables:

- API de reportes mas clara.
- Documentacion OpenAPI separada para `reporting`.

### Dia 4

- Diseñar la salida de `identity-service`.
- Definir endpoints minimos: login, logout, validacion de token y aprovisionamiento de acceso.
- Identificar el punto exacto en el flujo de creacion de cliente donde debe dejar de existir dependencia sincronica fuerte con `auth`.

Entregables:

- Contrato HTTP de `identity-service`.
- Lista de adaptadores que habra que cambiar en el core.

### Dia 5

- Preparar la estructura inicial para separar repositorios.
- Definir que archivos, paquetes y configuraciones pertenecen a cada servicio.
- Separar configuraciones compartidas de las especificas de negocio.

Entregables:

- Mapa de carpetas por servicio.
- Checklist para crear repos sin llevar basura tecnica.

### Dia 6

- Hacer limpieza de pruebas, docs y configuraciones.
- Confirmar que el monolito sigue estable.
- Revisar naming, convenciones y dependencias.

Entregables:

- Rama con base limpia.
- Pruebas relevantes en verde.

### Dia 7

- Abrir los repos nuevos solo si ya estan claros los contratos y ownership.
- Dejar el backlog de la semana siguiente.
- Preparar material de sustentacion o demo si aplica.

Entregables:

- Decision tomada sobre apertura de repos.
- Siguiente sprint definido.

## Repositorios objetivo

No hace falta crear los repos el primer dia. La recomendacion es abrirlos cuando ya esten estables los contratos y el reparto de responsabilidades.

### Repos que deberian existir

- `banco-digital-core-banking`
- `banco-digital-identity`
- `banco-digital-reporting`

## Criterio para abrir los repos nuevos

Abrir los repos nuevos cuando se cumplan estas condiciones:

1. El contrato entre servicios ya no cambie cada rato.
2. El ownership de paquetes y configuraciones este claro.
3. El flujo de autenticacion del core no dependa de clases internas del modulo `auth`.
4. El modulo `reporting` ya este lo bastante aislado como para moverse con poco ruido.

## Riesgos principales

- Intentar extraer `identity` sin terminar de limpiar dependencias internas.
- Separar `reporting` sin cerrar primero sus necesidades de datos.
- Abrir repos demasiado temprano y terminar duplicando cambios.
- Querer mover `transactions` fuera del core en esta misma semana.

## Recomendacion practica

Esta semana deberia enfocarse en dejar lista la extraccion, no en perseguir una separacion completa a cualquier costo. Si al final de la semana los contratos estan claros, el monolito esta mas limpio y `reporting` ya quedo bien delimitado, la base para abrir los repos nuevos va a ser mucho mejor.
