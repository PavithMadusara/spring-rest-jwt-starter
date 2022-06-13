# Package Stage
# Remember to Build Before This Stage
ARG SERVER_PORT=80

FROM eclipse-temurin:17-alpine
COPY target/app.jar /usr/local/lib/app.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
