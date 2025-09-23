FROM openjdk:17
COPY ./target/app-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "app-jar-with-dependencies.jar"]
