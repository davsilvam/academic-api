FROM eclipse-temurin:17.0.9_9-jdk-jammy
COPY . .
RUN ./mvnw clean install -DskipTests
ENTRYPOINT ["java","-jar","target/academic-1.0-SNAPSHOT.jar"]