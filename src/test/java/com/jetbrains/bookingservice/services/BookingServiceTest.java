package com.jetbrains.bookingservice.services;

import com.jetbrains.bookingservice.views.BookingResponseView;
import com.jetbrains.bookingservice.models.Booking;
import com.jetbrains.bookingservice.repositories.BookingRepository;
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
      BookingResponseView bookingResponseView = bookingService.getBookingsForRestaurant(restaurantId);

      // Assert
      assertEquals(HttpStatus.OK, bookingResponseView.getHttpStatus());
      assertEquals(false, bookingResponseView.hasError());
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
      BookingResponseView bookingResponseView = new BookingResponseView(newBooking, "Error error", HttpStatus.CONFLICT);
      Mockito.when(bookingValidator.validate(any(), anyString())).thenReturn(bookingResponseView);

      // Action
      BookingResponseView actualBookingResponseView = bookingService.createBooking(newBooking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CONFLICT, actualBookingResponseView.getHttpStatus());
      assertEquals(true, actualBookingResponseView.hasError());
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
      BookingResponseView bookingResponseView = new BookingResponseView(newBooking, null, null);
      Mockito.when(bookingValidator.validate(any(), anyString())).thenReturn(bookingResponseView);
      Mockito.when(bookingRepository.save(any())).thenReturn(newBooking);

      // Action
      BookingResponseView actualBookingResponseView = bookingService.createBooking(newBooking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CREATED, actualBookingResponseView.getHttpStatus());
      assertEquals(false, actualBookingResponseView.hasError());
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
      BookingResponseView actualBookingResponseView = bookingService.findAllByRestaurantIdAndDate(restaurantId, date);

      // Assert
      assertEquals(HttpStatus.OK, actualBookingResponseView.getHttpStatus());
      assertEquals(false, actualBookingResponseView.hasError());
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
      BookingResponseView actualBookingResponseView = bookingService.findAllByRestaurantIdAndDate(restaurantId, date);

      // Assert
      assertEquals(HttpStatus.NOT_FOUND, actualBookingResponseView.getHttpStatus());
      assertEquals(true, actualBookingResponseView.hasError());
    }
}
