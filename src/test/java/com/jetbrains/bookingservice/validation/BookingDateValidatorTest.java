package com.jetbrains.bookingservice.validation;

import com.jetbrains.bookingservice.Booking;
import com.jetbrains.bookingservice.Restaurant;
import com.jetbrains.bookingservice.RestaurantClosedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.TUESDAY;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingDateValidatorTest {
    private final BookingDateValidator bookingDateValidator = new BookingDateValidator();

    @Test
    @DisplayName("Should reject a booking for a day the restaurant is not open")
    void shouldRejectABookingForADayTheRestaurantIsNotOpen() {
        Restaurant restaurant = new Restaurant("99", 20, Set.of(MONDAY, TUESDAY));
        Booking newBooking = new Booking("99", LocalDate.of(2021, 4, 25), 10);

        // expect:
        assertThrows(RestaurantClosedException.class, () -> bookingDateValidator.validate(newBooking, restaurant));
    }
}