package com.jetbrains.bookingservice;

import com.jetbrains.bookingservice.validation.BookingCapacityValidator;
import com.jetbrains.bookingservice.validation.BookingDateValidator;
import com.jetbrains.bookingservice.validation.RestaurantValidator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

        new RestaurantValidator().validate(restaurant);
        //noinspection ConstantConditions - this is not null here because the RestaurantValidator should ensure it is not
        new BookingDateValidator().validate(booking, restaurant);
        new BookingCapacityValidator().validate(booking, restaurant, repository.findAllByRestaurantIdAndDate(booking.getRestaurantId(), booking.getDate()));

        // if we got this far, the booking is valid and we can save it
        return repository.save(booking);
    }
}
