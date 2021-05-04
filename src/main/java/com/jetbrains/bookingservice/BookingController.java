package com.jetbrains.bookingservice;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class BookingController {

    private final BookingRepository repository;

    public BookingController(final BookingRepository repository) {
        this.repository = repository;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @PostMapping("/bookings")
    public Booking createBooking(@RequestBody Booking booking, RestTemplate restTemplate) {
        // In a "real" environment this would at the very least be a property/environment variable, but ideally something like Service Discovery like Eureka
        Restaurant restaurant = restTemplate.getForObject("http://localhost:8080/restaurants/" + booking.getRestaurantId(), Restaurant.class);

        // check if the restaurant actually exists 
        throwExceptionIfRestaurantNotValid(restaurant, booking.getRestaurantId());

        // check if the number of diners in the booking is more than the number of seats in the restaurant
        if (restaurant.capacity() < booking.getNumberOfDiners()) {
            throw new NoAvailableCapacityException("Number of diners exceeds available restaurant capacity");
        }

        // check the restaurant is open on the day of the booking
        if (!restaurant.openingDays().contains(booking.getDate().getDayOfWeek())) {
            throw new RestaurantClosedException("Restaurant is not open on: " + booking.getDate());
        }

        // find all the bookings for that day and check that with all the booked diners the restaurant still has space for the new booking diners
        List<Booking> allByRestaurantIdAndDate = repository.findAllByRestaurantIdAndDate(booking.getRestaurantId(), booking.getDate());
        int totalDinersOnThisDay = allByRestaurantIdAndDate.stream().mapToInt(Booking::getNumberOfDiners).sum();
        if (totalDinersOnThisDay + booking.getNumberOfDiners() > restaurant.capacity()) {
            throw new NoAvailableCapacityException("Restaurant all booked up!");
        }

        // if we got this far, the booking is valid and we can save it
        return repository.save(booking);
    }

    private void throwExceptionIfRestaurantNotValid(@Nullable Restaurant restaurant, String restaurantId) {
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

}
