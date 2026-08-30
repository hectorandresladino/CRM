FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/crm-backend-1.0.0.jar app.jar

# Variables de entorno para OpenShift (se sobrescriben con secrets)
ENV SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
ENV SPRING_JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
ENV SPRING_H2_CONSOLE=false

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
