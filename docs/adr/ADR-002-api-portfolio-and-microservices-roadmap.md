# ADR-002: Portafolio de APIs y Ruta de Migracion Gradual a Microservicios

| Campo | Valor |
|-------|-------|
| **Estado** | Propuesto |
| **Fecha** | 2026-04-09 |
| **Autores** | Equipo Banco Digital |
| **Contexto** | Necesidad academica de implementar y documentar al menos 3 APIs |

---

## 1 Contexto

El backend ya esta organizado como un monolito modular con limites claros entre `auth`, `customers` y `accounts`. A partir del requisito de implementar y documentar por lo menos 3 APIs, aparecio la duda de si era necesario migrar de inmediato a microservicios o si era mas conveniente consolidar primero los limites del sistema actual.

## 2 Decision

Por ahora no se realizara una migracion inmediata a microservicios. En esta iteracion se hara lo siguiente:

1. Se formalizan tres APIs REST por dominio dentro del monolito modular.
2. Se documentan de forma independiente mediante grupos OpenAPI.
3. Se define una hoja de ruta de extraccion progresiva para cuando exista una necesidad real de despliegue independiente.

## 3 APIs reconocidas como productos independientes

| API | Dominio | Responsabilidad |
|-----|---------|-----------------|
| API de Autenticacion | `auth` | Login, emision de JWT, logout y revocacion de token |
| API de Clientes | `customers` | Registro, consulta y actualizacion de perfiles |
| API de Cuentas | `accounts` | Apertura de cuentas, consulta de saldo y consolidado |

## 4 Justificacion

### 4.1 Por que no migrar todavia

- Para el alcance actual, microservicios implicaria asumir mas complejidad operativa: pipelines separados, observabilidad distribuida, contratos entre servicios y manejo de fallos de red.
- Los limites modulares ya existen en el codigo, asi que es posible demostrar separacion funcional sin reescribir todo el backend.
- Hacer la migracion antes de tiempo aumentaria el riesgo tecnico y el esfuerzo de entrega sin aportar un beneficio claro frente al criterio academico actual.

### 4.2 Que se gana con esta decision

- Cumplimiento inmediato del requisito de 3 APIs.
- Documentacion separada por dominio, util para la sustentacion y la evaluacion.
- Un punto de partida mas ordenado para extraer servicios mas adelante.

## 5 Ruta de migracion recomendada

### Fase 1: Endurecer los limites del monolito modular

- Evitar dependencias directas entre modulos por clases concretas.
- Comunicar modulos mediante puertos, eventos de dominio o contratos internos.
- Mantener documentacion OpenAPI por dominio.

### Fase 2: Preparar la separacion operativa

- Introducir configuracion externa por servicio.
- Definir ownership de datos por modulo.
- Identificar integraciones sincrona vs asincrona.

### Fase 3: Extraer microservicios

- Extraer primero `auth`, luego evaluar la salida del core y de los servicios de lectura.
- Incorporar API Gateway y service-to-service auth.
- Separar bases de datos o esquemas por servicio solo cuando la operacion lo justifique.

## 6 Consecuencias

### Positivas

- Menor riesgo de regresion en la entrega actual.
- Mejor trazabilidad entre dominio, endpoints y documentacion.
- Transicion futura mas ordenada.

### Negativas

- El despliegue sigue siendo unico en esta etapa.
- La independencia operativa entre dominios aun no existe.

## 7 Criterio de salida hacia microservicios

La extraccion deja de ser una mejora deseable y pasa a ser una prioridad cuando se cumplan al menos dos de estos factores:

- Equipos distintos modifican modulos diferentes con alta frecuencia.
- Necesidades de escalamiento por dominio son muy diferentes.
- Se requieren despliegues independientes por regulacion o disponibilidad.
- La carga transaccional o de seguridad de `auth` exige aislamiento operativo.
