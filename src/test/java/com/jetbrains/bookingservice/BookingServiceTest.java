package com.jetbrains.bookingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private static final LocalDate date = LocalDate.of(2021, Month.SEPTEMBER, 21);

    @Test
    @DisplayName("Test GetBookingsForRestaurant should get all bookings for a given restaurant")
    void testGetBookingsForRestaurant_shouldGetAllBookingsForAGivenRestaurant(@Mock BookingRepository bookingRepository,
                                                                              @Mock BookingValidator bookingValidator) {
      // Arrange
      BookingService bookingService = new BookingService(bookingRepository, bookingValidator);
      String restaurantId = "2";
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
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
      int numberOfDiners = 10;
      Booking newBooking = new Booking(restaurantId, date, numberOfDiners);
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
      int numberOfDiners = 10;
      Booking newBooking = new Booking(restaurantId, date, numberOfDiners);
      BookingResponse bookingResponse = new BookingResponse(newBooking, null, null);
      Mockito.when(bookingValidator.validate(any(), anyString())).thenReturn(bookingResponse);
      Mockito.when(bookingRepository.save(any())).thenReturn(newBooking);

      // Action
      BookingResponse actualBookingResponse = bookingService.createBooking(newBooking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CREATED, actualBookingResponse.getHttpStatus());
      assertEquals(false, actualBookingResponse.hasError());
    }

    @Test
    @DisplayName("Test FindAllByRestaurantIdAndDate should return booking response without error")
    void testFindAllByRestaurantIdAndDate_shouldReturnCorrectBookingResponse(@Mock BookingRepository bookingRepository,
                                                              @Mock BookingValidator bookingValidator) {
      // Arrange
      BookingService bookingService = new BookingService(bookingRepository, bookingValidator);
      String restaurantId = "2";
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
      Mockito.when(bookingRepository.findAllByRestaurantIdAndDate(anyString(), any())).thenReturn(singletonList(booking));

      // Action
      BookingResponse actualBookingResponse = bookingService.findAllByRestaurantIdAndDate(restaurantId, date);

      // Assert
      assertEquals(HttpStatus.OK, actualBookingResponse.getHttpStatus());
      assertEquals(false, actualBookingResponse.hasError());
    }

    @Test
    @DisplayName("Test FindAllByRestaurantIdAndDate should return booking response with 404 error")
    void testFindAllByRestaurantIdAndDate_shouldReturnBookingResponseWithNotFoundError(@Mock BookingRepository bookingRepository,
                                                                             @Mock BookingValidator bookingValidator) {
      // Arrange
      BookingService bookingService = new BookingService(bookingRepository, bookingValidator);
      String restaurantId = "2";
      Mockito.when(bookingRepository.findAllByRestaurantIdAndDate(anyString(), any())).thenReturn(Collections.EMPTY_LIST);

      // Action
      BookingResponse actualBookingResponse = bookingService.findAllByRestaurantIdAndDate(restaurantId, date);

      // Assert
      assertEquals(HttpStatus.NOT_FOUND, actualBookingResponse.getHttpStatus());
      assertEquals(true, actualBookingResponse.hasError());
    }
}
