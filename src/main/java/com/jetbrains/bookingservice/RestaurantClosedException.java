package com.jetbrains.bookingservice;

//TODO: make this a Conflict(?) and create an integration test for it
public class RestaurantClosedException extends BookingException {
    public RestaurantClosedException(final String message) {
        super(message);
    }
}
