# Étape 1 : Build du JAR avec Maven
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copie d'abord le wrapper Maven et le pom pour profiter du cache Docker
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Rend le script exécutable (important sur Alpine)
RUN chmod +x mvnw

# Télécharge les dépendances (couche cachable si pom.xml inchangé)
RUN ./mvnw dependency:go-offline

# Copie le code source
COPY src ./src

# Build final du JAR
RUN ./mvnw clean package -DskipTests

# Étape 2 : Image finale légère
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copie uniquement le JAR construit depuis l'étape précédente
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]