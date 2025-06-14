FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Fix mvnw permissions
RUN chmod +x ./mvnw

RUN ./mvnw dependency:resolve

COPY . .

# Fix permissions again after copying everything
RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/trafficflow-0.0.1-SNAPSHOT.jar"]