# Etapa 1: Compilar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY backend/src ./backend/src

RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 1544
ENTRYPOINT ["java", "-jar", "app.jar"]