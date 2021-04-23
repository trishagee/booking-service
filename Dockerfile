FROM openjdk:16-jdk-alpine
COPY build/libs/BookingService-0.0.1-SNAPSHOT.jar booking-service.jar
ENTRYPOINT ["java", "-jar", "/booking-service.jar"]