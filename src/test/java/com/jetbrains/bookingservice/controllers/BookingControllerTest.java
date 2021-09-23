package com.jetbrains.bookingservice.controllers;

import com.jetbrains.bookingservice.views.BookingResponseView;
import com.jetbrains.bookingservice.models.Booking;
import com.jetbrains.bookingservice.services.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Test
    @DisplayName("Test createBooking should create a booking when all validations are passed")
    void testCreateBooking_shouldCreateBookingWhenAllValidationsPassed(@Mock BookingService bookingService) {
        // Arrange
        BookingController bookingController = new BookingController(bookingService);
        String restaurantId = "2";
        LocalDate now = LocalDate.now();
        int numberOfDiners = 10;
        Booking booking = new Booking(restaurantId, now, numberOfDiners);
        Mockito.when(bookingService.createBooking(any(), anyString()))
            .thenReturn(new BookingResponseView(booking, null, HttpStatus.CREATED));
        Booking newBooking = new Booking(restaurantId, now, numberOfDiners);

        // Action
        BookingResponseView bookingResponseView = bookingController.createBooking(newBooking, restaurantId);

        // Assert
        assertEquals(HttpStatus.CREATED, bookingResponseView.getHttpStatus());
        assertEquals(false, bookingResponseView.hasError());
    }

    @Test
    @DisplayName("Test createBooking should not create a booking when number of diners exceeds available restaurant capacity")
    void testCreateBooking_shouldNotCreateBookingWhenDinersExceedsRestaurantCapacity(@Mock BookingService bookingService) {
        // Arrange
        BookingController bookingController = new BookingController(bookingService);
        String restaurantId = "2";
        LocalDate now = LocalDate.now();
        int numberOfDiners = 10;
        String expectedErrorMessage = "Number of diners exceeds available restaurant capacity";
        BookingResponseView expectedBookingResponseView = new BookingResponseView(expectedErrorMessage
            , HttpStatus.CONFLICT);
        Mockito.when(bookingService.createBooking(any(), anyString())).thenReturn(expectedBookingResponseView);
        Booking newBooking = new Booking(restaurantId, now, numberOfDiners);

        // Action
        BookingResponseView actualBookingResponseView = bookingController.createBooking(newBooking, restaurantId);

        // Assert
        assertEquals(HttpStatus.CONFLICT, actualBookingResponseView.getHttpStatus());
        assertEquals(true, actualBookingResponseView.hasError());
        assertEquals(true, actualBookingResponseView.isErrorMessageEqualTo(expectedErrorMessage));
    }

    @Test
    @DisplayName("Test getBookingsForRestaurant should return booking list for a given restaurant id")
    void testGetBookingsForRestaurant_shouldReturnBookingListForARestaurant(@Mock BookingService bookingService) {
        // Arrange
        BookingController bookingController = new BookingController(bookingService);
        String restaurantId = "2";
        LocalDate now = LocalDate.now();
        int numberOfDiners = 10;
        Booking expectedBooking = new Booking(restaurantId, now, numberOfDiners);
        List<Booking> bookings = singletonList(expectedBooking);
        BookingResponseView expectedBookingResponseView = new BookingResponseView(bookings, null, HttpStatus.OK);
        Mockito.when(bookingService.getBookingsForRestaurant(anyString())).thenReturn(expectedBookingResponseView);

        // Action
        BookingResponseView actualBookingResponseView = bookingController.getBookingsForRestaurant(restaurantId);

        // Assert
        assertEquals(HttpStatus.OK, actualBookingResponseView.getHttpStatus());
        assertEquals(false, actualBookingResponseView.hasError());
    }

    @Test
    @DisplayName("Test getBookingsForRestaurantByDate should return booking list for a given restaurant id and date")
    void testGetBookingsForRestaurantByDate_shouldReturnBookingListForARestaurantAndGivenDate(@Mock BookingService bookingService) {
        // Arrange
        BookingController bookingController = new BookingController(bookingService);
        String restaurantId = "2";
        LocalDate now = LocalDate.now();
        int numberOfDiners = 10;
        Booking expectedBooking = new Booking(restaurantId, now, numberOfDiners);
        List<Booking> bookings = singletonList(expectedBooking);
        BookingResponseView expectedBookingResponseView = new BookingResponseView(bookings, null, HttpStatus.OK);
        Mockito.when(bookingService.findAllByRestaurantIdAndDate(anyString(), any())).thenReturn(expectedBookingResponseView);

        // Action
        BookingResponseView actualBookingResponseView = bookingController.getBookingsForRestaurantByDate(restaurantId, now);

        // Assert
        assertEquals(HttpStatus.OK, actualBookingResponseView.getHttpStatus());
        assertEquals(false, actualBookingResponseView.hasError());
    }
}
