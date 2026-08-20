# ---- Stage 1 : BUILD ----
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve -q

COPY src src
RUN ./mvnw package -DskipTests -q

# ---- Stage 2 : RUN ----
# Image légère avec juste le JRE (pas de JDK, pas de Maven)
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copier uniquement le JAR compilé depuis le stage build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
