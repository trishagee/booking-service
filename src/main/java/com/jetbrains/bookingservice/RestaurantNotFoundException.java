package com.jetbrains.bookingservice;

//TODO: make this a 404 and create an integration test for it
public class RestaurantNotFoundException extends BookingException {
    public RestaurantNotFoundException(final String message) {
        super(message);
    }

    public RestaurantNotFoundException() {
        super("No restaurant found");
    }
}
