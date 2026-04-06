# ════════════════════════════════════════════════════
# Stage 1: Build
# ════════════════════════════════════════════════════
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
#instalar bash
RUN apk add --no-cache bash


COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -q


COPY src ./src
RUN ./mvnw package -DskipTests -q

# ════════════════════════════════════════════════════
# Stage 2: Runtime (imagen mínima)
# ════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
