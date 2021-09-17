package com.jetbrains.bookingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Test
    @DisplayName("Test GetBookingsForRestaurant should get all bookings for a given restaurant")
    void testGetBookingsForRestaurant_shouldGetAllBookingsForAGivenRestaurant(@Mock BookingRepository bookingRepository,
                                                                              @Mock BookingValidator bookingValidator) {
      // Arrange
      BookingService bookingService = new BookingService(bookingRepository, bookingValidator);
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      Mockito.when(bookingRepository.findAllByRestaurantId(anyString()))
          .thenReturn(singletonList(booking));

      // Action
      BookingResponse bookingResponse = bookingService.getBookingsForRestaurant(restaurantId);

      // Assert
      assertEquals(HttpStatus.OK, bookingResponse.getHttpStatus());
      assertEquals(false, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test createBooking should return booking response with error")
    void testCreateBooking_shouldReturnErrorBookingResponse(@Mock BookingRepository bookingRepository,
                                                            @Mock BookingValidator bookingValidator) {
      // Arrange
      BookingService bookingService = new BookingService(bookingRepository, bookingValidator);
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking newBooking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(newBooking, "Error error", HttpStatus.CONFLICT);
      Mockito.when(bookingValidator.validate(any(), anyString())).thenReturn(bookingResponse);

      // Action
      BookingResponse actualBookingResponse = bookingService.createBooking(newBooking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CONFLICT, actualBookingResponse.getHttpStatus());
      assertEquals(true, actualBookingResponse.hasError());
    }

    @Test
    @DisplayName("Test getBookingsForRestaurant should return booking response without error")
    void testCreateBooking_shouldReturnCorrectBookingResponse(@Mock BookingRepository bookingRepository,
                                                              @Mock BookingValidator bookingValidator) {
      // Arrange
      BookingService bookingService = new BookingService(bookingRepository, bookingValidator);
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking newBooking = new Booking(restaurantId, now, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(newBooking, "", null);
      Mockito.when(bookingValidator.validate(any(), anyString())).thenReturn(bookingResponse);
      Mockito.when(bookingRepository.save(any())).thenReturn(newBooking);

      // Action
      BookingResponse actualBookingResponse = bookingService.createBooking(newBooking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CREATED, actualBookingResponse.getHttpStatus());
      assertEquals(false, actualBookingResponse.hasError());
    }
}