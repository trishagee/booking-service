package com.jetbrains.bookingservice;

public class NoAvailableCapacityException extends BookingException {
    public NoAvailableCapacityException(final String message) {
        super(message);
    }
}
