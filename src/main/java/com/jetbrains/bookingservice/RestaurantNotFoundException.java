package com.jetbrains.bookingservice;

public class RestaurantNotFoundException extends BookingException{
    public RestaurantNotFoundException(final String message) {
        super(message);
    }
}
