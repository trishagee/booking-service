package com.jetbrains.bookingservice;

public class RestaurantClosedException extends BookingException {
    public RestaurantClosedException(final String message) {
        super(message);
    }
}
