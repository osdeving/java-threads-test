FROM openjdk:11-jre-slim
WORKDIR /app
COPY ./myapp.jar /app/
CMD ["java", "-Xss512k", "-Xms4g", "-Xms4g", "-jar", "myapp.jar"]

