package com.jetbrains.bookingservice.validation;

import com.jetbrains.bookingservice.Booking;
import com.jetbrains.bookingservice.NoAvailableCapacityException;
import com.jetbrains.bookingservice.Restaurant;

import java.util.List;

public class BookingCapacityValidator {
    public void validate(Booking booking, Restaurant restaurant, List<Booking> bookingsThatDay) {
        throwExceptionIfBookingHasMoreDinersThanRestaurantHasSeats(booking, restaurant);
        throwExceptionIfRestaurantDoesNotHaveEnoughSpaceOnThatDate(booking, restaurant, bookingsThatDay);
    }

    private void throwExceptionIfBookingHasMoreDinersThanRestaurantHasSeats(Booking booking, Restaurant restaurant) {
        if (restaurant.capacity() < booking.getNumberOfDiners()) {
            throw new NoAvailableCapacityException("Number of diners exceeds available restaurant capacity");
        }
    }

    private void throwExceptionIfRestaurantDoesNotHaveEnoughSpaceOnThatDate(Booking booking, Restaurant restaurant, List<Booking> bookingsThatDay) {
        int totalDinersOnThisDay = bookingsThatDay.stream().mapToInt(Booking::getNumberOfDiners).sum();
        if (totalDinersOnThisDay + booking.getNumberOfDiners() > restaurant.capacity()) {
            throw new NoAvailableCapacityException("Restaurant all booked up!");
        }
    }
}
