FROM eclipse-temurin:25-alpine
COPY build/libs/BookingService.jar booking-service.jar
ENTRYPOINT ["java", "-jar", "/booking-service.jar"]