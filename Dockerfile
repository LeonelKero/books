# BUILD STAGE
FROM maven:3.9-eclipse-temurin-21 AS build
LABEL MAINTAINER=waboleonel@gmail.com
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
RUN ./mvnw dependency:go-offline -B
COPY src /app/src
RUN ./mvnw clean package
RUN echo $(ls target/*.jar) > /app/jarfile.txt

# RUNTIME STAGE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar book-api.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar book-api.jar"]
