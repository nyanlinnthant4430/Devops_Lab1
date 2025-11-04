FROM eclipse-temurin:21-jdk



COPY ./target/devops.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "devops.jar"]
