package com.jetbrains.bookingservice.validation;

import com.jetbrains.bookingservice.Booking;
import com.jetbrains.bookingservice.Restaurant;
import com.jetbrains.bookingservice.RestaurantClosedException;

public class BookingDateValidator {
    public void validate(Booking booking, Restaurant restaurant) {
        if (!restaurant.openingDays().contains(booking.getDate().getDayOfWeek())) {
            throw new RestaurantClosedException("Restaurant is not open on: " + booking.getDate());
        }
    }
}
