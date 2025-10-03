FROM maven:4.0.0-rc-4-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl gymapp-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/gymapp-service/target/gymapp-service-*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]