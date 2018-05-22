FROM openjdk:8-jdk-alpine
#FROM alpine-java:base
#ENV DATASTORE_DATASET=rgt-ssms
#ENV DATASTORE_EMULATOR_HOST=localhost:8081
#ENV DATASTORE_EMULATOR_HOST_PATH=localhost:8081/datastore
#ENV DATASTORE_HOST=http://localhost:8081
#ENV DATASTORE_PROJECT_ID=rgt-ssms
#ENV DATASTORE_USE_PROJECT_ID_AS_APP_ID=true
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]