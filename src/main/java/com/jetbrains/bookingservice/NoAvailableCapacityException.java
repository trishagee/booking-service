package com.jetbrains.bookingservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "NoAvailableCapacityException")
public class NoAvailableCapacityException extends BookingException {
    public NoAvailableCapacityException(final String message) {
        super(message);
    }
}
