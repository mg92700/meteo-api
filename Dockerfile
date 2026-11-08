# ══════════════════════════════════════════════════════════
# meteo-api : image multi-stage (build Maven puis JRE seul), utilisateur non-root
#   docker build -t meteo-api .
#   docker run -p 8081:8081 --env-file .env meteo-api
# ══════════════════════════════════════════════════════════
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp dependency:go-offline -q || true
COPY src src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp package -DskipTests && cp target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${PORT:-8081}/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
