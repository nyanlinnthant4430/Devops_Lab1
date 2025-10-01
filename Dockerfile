FROM openjdk:17
COPY ./target/DevOpsLab3-0.1.0.4-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "DevOpsLab3-0.1.0.4-jar-with-dependencies.jar"]
