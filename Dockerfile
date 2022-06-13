# Build Stage
FROM maven:3.8.4-eclipse-temurin-17-alpine as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Package Stage
ARG SERVER_PORT=80

FROM eclipse-temurin:17-alpine
COPY --from=build /home/app/target/app.jar /usr/local/lib/app.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
