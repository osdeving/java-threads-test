FROM openjdk:17-slim
WORKDIR /app
COPY ./target/java-threads-test.jar /app/
CMD ["java", "-Xss512k", "-Xms4g", "-Xms4g", "-jar", "java-threads-test.jar"]

