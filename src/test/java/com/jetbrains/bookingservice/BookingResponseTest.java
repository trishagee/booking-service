package com.jetbrains.bookingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingResponseTest {
    @Test
    @DisplayName("Test hasError should return false")
    void testHasError_shouldReturnFalse() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(booking, "", HttpStatus.CREATED);

      // Action & Assert
      assertEquals(false, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test hasError should return true")
    void testHasError_shouldReturnTrue() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(booking, "I am error", HttpStatus.CONFLICT);

      // Action & Assert
      assertEquals(true, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test testIsErrorMessageEqualTo should return true")
    void testIsErrorMessageEqualTo_shouldReturnTrue() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(booking, "", HttpStatus.CREATED);

      // Action & Assert
      assertEquals(true, bookingResponse.isErrorMessageEqualTo(""));
    }

    @Test
    @DisplayName("Test testIsErrorMessageEqualTo should return false")
    void testIsErrorMessageEqualTo_shouldReturnFalse() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(booking, "I am error", HttpStatus.CONFLICT);

      // Action & Assert
      assertEquals(false, bookingResponse.isErrorMessageEqualTo("I am not error"));
    }
}