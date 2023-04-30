FROM openjdk:latest
WORKDIR /app
COPY ./target/*.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]

