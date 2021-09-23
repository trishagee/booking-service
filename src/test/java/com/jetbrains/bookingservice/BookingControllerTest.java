package com.jetbrains.bookingservice;

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
            .thenReturn(new BookingResponse(booking, null, HttpStatus.CREATED));
        Booking newBooking = new Booking(restaurantId, now, numberOfDiners);

        // Action
        BookingResponse bookingResponse = bookingController.createBooking(newBooking, restaurantId);

        // Assert
        assertEquals(HttpStatus.CREATED, bookingResponse.getHttpStatus());
        assertEquals(false, bookingResponse.hasError());
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
        BookingResponse expectedBookingResponse = new BookingResponse(expectedErrorMessage
            , HttpStatus.CONFLICT);
        Mockito.when(bookingService.createBooking(any(), anyString())).thenReturn(expectedBookingResponse);
        Booking newBooking = new Booking(restaurantId, now, numberOfDiners);

        // Action
        BookingResponse actualBookingResponse = bookingController.createBooking(newBooking, restaurantId);

        // Assert
        assertEquals(HttpStatus.CONFLICT, actualBookingResponse.getHttpStatus());
        assertEquals(true, actualBookingResponse.hasError());
        assertEquals(true, actualBookingResponse.isErrorMessageEqualTo(expectedErrorMessage));
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
        BookingResponse expectedBookingResponse = new BookingResponse(bookings, null, HttpStatus.OK);
        Mockito.when(bookingService.getBookingsForRestaurant(anyString())).thenReturn(expectedBookingResponse);

        // Action
        BookingResponse actualBookingResponse = bookingController.getBookingsForRestaurant(restaurantId);

        // Assert
        assertEquals(HttpStatus.OK, actualBookingResponse.getHttpStatus());
        assertEquals(false, actualBookingResponse.hasError());
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
        BookingResponse expectedBookingResponse = new BookingResponse(bookings, null, HttpStatus.OK);
        Mockito.when(bookingService.findAllByRestaurantIdAndDate(anyString(), any())).thenReturn(expectedBookingResponse);

        // Action
        BookingResponse actualBookingResponse = bookingController.getBookingsForRestaurantByDate(restaurantId, now);

        // Assert
        assertEquals(HttpStatus.OK, actualBookingResponse.getHttpStatus());
        assertEquals(false, actualBookingResponse.hasError());
    }
}
