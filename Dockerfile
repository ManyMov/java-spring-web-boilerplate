FROM maven:3.9-eclipse-temurin-20 as builder
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN --mount=type=cache,target=/root/.m2 mvn clean package

FROM eclipse-temurin:20-alpine
COPY --from=builder /usr/src/app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "/app/app.jar"]