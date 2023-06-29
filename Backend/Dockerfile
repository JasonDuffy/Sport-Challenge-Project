FROM openjdk:latest
WORKDIR /app
COPY ./target/*.jar /app/app.jar
EXPOSE 8081
ENV TZ=Europe/Berlin
ENV SCP_PRODUCTION=true
CMD ["java", "-jar", "/app/app.jar"]

