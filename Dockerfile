FROM openjdk:17
WORKDIR /app
COPY ./target/app-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
