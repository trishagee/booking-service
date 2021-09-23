package com.jetbrains.bookingservice.views;

import com.jetbrains.bookingservice.models.Booking;
import com.jetbrains.bookingservice.views.BookingResponseView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingResponseViewTest {
    @Test
    @DisplayName("Test hasError should return false")
    void testHasError_shouldReturnFalseWhenErrorMessageIsNull() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponseView bookingResponseView = new BookingResponseView(booking, null, HttpStatus.CREATED);

      // Action & Assert
      assertEquals(false, bookingResponseView.hasError());
    }

  @Test
  @DisplayName("Test hasError should return false")
  void testHasError_shouldReturnFalseWhenErrorMessageIsEmptyString() {
    // Arrange
    String restaurantId = "2";
    LocalDate now = LocalDate.now();
    int numberOfDiners = 10;
    Booking booking = new Booking(restaurantId, now, numberOfDiners);
    BookingResponseView bookingResponseView = new BookingResponseView(booking, null, HttpStatus.CREATED);

    // Action & Assert
    assertEquals(false, bookingResponseView.hasError());
  }

    @Test
    @DisplayName("Test hasError should return true")
    void testHasError_shouldReturnTrue() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponseView bookingResponseView = new BookingResponseView(booking, "I am error", HttpStatus.CONFLICT);

      // Action & Assert
      assertEquals(true, bookingResponseView.hasError());
    }

    @Test
    @DisplayName("Test testIsErrorMessageEqualTo should return true")
    void testIsErrorMessageEqualTo_shouldReturnTrue() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponseView bookingResponseView = new BookingResponseView(booking, null, HttpStatus.CREATED);

      // Action & Assert
      assertEquals(true, bookingResponseView.isErrorMessageEqualTo(null));
    }

    @Test
    @DisplayName("Test testIsErrorMessageEqualTo should return false")
    void testIsErrorMessageEqualTo_shouldReturnFalse() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponseView bookingResponseView = new BookingResponseView(booking, "I am error", HttpStatus.CONFLICT);

      // Action & Assert
      assertEquals(false, bookingResponseView.isErrorMessageEqualTo("I am not error"));
    }
}