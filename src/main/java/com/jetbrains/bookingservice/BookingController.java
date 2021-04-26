package com.jetbrains.bookingservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final BookingRepository repository;

    public BookingController(final BookingRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/bookings")
    public Booking createBooking(@RequestBody Booking booking) {
        return repository.save(booking);
    }
}
