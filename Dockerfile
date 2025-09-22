FROM openjdk:19
LABEL authors="Nyan"
COPY ./target/classes/imc /tmp/imc
WORKDIR /tmp
ENTRYPOINT ["java", "imc.com.App"]