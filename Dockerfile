FROM eclipse-temurin:17.0.8.1_1-jdk-jammy
COPY . .
RUN chmod +x ./mvnw && ./mvnw clean install -DskipTests
ENTRYPOINT ["java","-jar","target/academic-1.0-SNAPSHOT.jar"]