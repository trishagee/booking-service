package com.jetbrains.bookingservice;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(final BookingService bookingService) {
        this.bookingService= bookingService;
    }

    @GetMapping("/restaurants/{restaurantId}/bookings")
    public BookingResponse getBookingsForRestaurant(@PathVariable String restaurantId) {
        return bookingService.getBookingsForRestaurant(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/bookings/{date}")
    public List<Booking> getBookingsForRestaurantByDate(@PathVariable String restaurantId,
                                                        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return repository.findAllByRestaurantIdAndDate(restaurantId, date);
    }

    @PostMapping("/restaurants/{restaurantId}/bookings")
    public BookingResponse createBooking(@RequestBody Booking booking, @PathVariable String restaurantId) {
        return bookingService.createBooking(booking, restaurantId);
    }
}
