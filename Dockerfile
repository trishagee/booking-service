FROM openjdk:16.0.1-jdk
COPY build/libs/BookingService.jar booking-service.jar
ENTRYPOINT ["java", "-jar", "/booking-service.jar"]