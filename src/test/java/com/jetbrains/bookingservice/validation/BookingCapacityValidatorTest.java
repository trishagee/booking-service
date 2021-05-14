package com.jetbrains.bookingservice.validation;

import com.jetbrains.bookingservice.Booking;
import com.jetbrains.bookingservice.NoAvailableCapacityException;
import com.jetbrains.bookingservice.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingCapacityValidatorTest {
    private final BookingCapacityValidator bookingCapacityValidator = new BookingCapacityValidator();

    @Test
    @DisplayName("Should reject if the number of diners is more than the number of restaurant seats")
    void shouldRejectIfTheNumberOfDinersIsMoreThanTheNumberOfRestaurantSeats() {
        // given
        Restaurant restaurant = new Restaurant("2", 5, Set.of());
        Booking newBooking = new Booking("2", now(), 10);

        // expect
        assertThrows(NoAvailableCapacityException.class,
                     () -> bookingCapacityValidator.validate(newBooking, restaurant, List.of()));
    }

    @Test
    @DisplayName("Should reject if the number of diners is more than the space available on that date")
    void shouldRejectIfTheNumberOfDinersIsMoreThanTheSpaceAvailableOnThatDate() {
        // given
        String restaurantId = "101";
        Restaurant restaurant = new Restaurant(restaurantId, 20, Set.of(MONDAY, FRIDAY));
        Booking newBooking = new Booking(restaurantId, LocalDate.of(2021, 4, 26), 4);

        List<Booking> bookingsThatDay = List.of(new Booking(restaurantId, LocalDate.of(2021, 4, 26), 10),
                                                new Booking(restaurantId, LocalDate.of(2021, 4, 26), 7));

        // expect:
        assertThrows(NoAvailableCapacityException.class,
                     () -> bookingCapacityValidator.validate(newBooking, restaurant, bookingsThatDay));
    }

}