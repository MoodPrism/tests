FROM anapsix/alpine-java:latest
COPY ./target/moosprism-0.0.1.jar ./root
WORKDIR ./root
ENTRYPOINT ["java", "-jar", "moosprism-0.0.1.jar"]
EXPOSE 8080
