FROM openjdk:latest
WORKDIR /app
COPY ./target/app.jar /app
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]

