package com.jetbrains.bookingservice;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {
    private final BookingRepository repository;
    private final RestaurantClient restaurantClient;

    public BookingController(final BookingRepository repository, final RestaurantClient restaurantClient) {
        this.repository = repository;
        this.restaurantClient = restaurantClient;
    }

    @GetMapping("/restaurants/{restaurantId}/bookings")
    public List<Booking> getBookingsForRestaurant(@PathVariable String restaurantId) {
        return repository.findAllByRestaurantId(restaurantId);
    }

    @PostMapping("/restaurants/{restaurantId}/bookings")
    public Booking createBooking(@RequestBody Booking booking, @PathVariable String restaurantId) {
        Restaurant restaurant = restaurantClient.getRestaurant(restaurantId);

        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        if (restaurant.capacity() < booking.getNumberOfDiners()) {
            throw new NoAvailableCapacityException("Number of diners exceeds available restaurant capacity");
        }

        if (!restaurant.openingDays().contains(booking.getDate().getDayOfWeek())) {
            throw new RestaurantClosedException("Restaurant is not open on: " + booking.getDate());
        }

        List<Booking> allByRestaurantIdAndDate = repository.findAllByRestaurantIdAndDate(restaurantId, booking.getDate());
        int totalDinersOnThisDay = allByRestaurantIdAndDate
                .stream().mapToInt(Booking::getNumberOfDiners).sum();
        if (totalDinersOnThisDay + booking.getNumberOfDiners() > restaurant.capacity()) {
            throw new NoAvailableCapacityException("Restaurant all booked up!");
        }

        return repository.save(booking);
    }

    public void deleteBooking(Booking booking) {
        repository.delete(booking);
    }
}
