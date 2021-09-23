package com.jetbrains.bookingservice.controllers;

import com.jetbrains.bookingservice.views.BookingResponseView;
import com.jetbrains.bookingservice.models.Booking;
import com.jetbrains.bookingservice.services.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(final BookingService bookingService) {
        this.bookingService= bookingService;
    }

    @GetMapping("/restaurants/{restaurantId}/bookings")
    public BookingResponseView getBookingsForRestaurant(@PathVariable String restaurantId) {
        return bookingService.getBookingsForRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/bookings/{date}")
    public BookingResponseView getBookingsForRestaurantByDate(@PathVariable String restaurantId,
                                                              @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return bookingService.findAllByRestaurantIdAndDate(restaurantId, date);
    }

    @PostMapping("/restaurants/{restaurantId}/bookings")
    public BookingResponseView createBooking(@RequestBody Booking booking, @PathVariable String restaurantId) {
        return bookingService.createBooking(booking, restaurantId);
    }
}
