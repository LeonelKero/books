FROM openjdk:21-jdk
WORKDIR /app
COPY pom.xml .
COPY src /app/src
RUN mvn clean package
EXPOSE 8080
CMD ["java", "-jar", "target/*.jar"]
