package com.jetbrains.bookingservice;

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
        Restaurant restaurant = restTemplate.getForObject("http://localhost:8080/restaurants/"+booking.getRestaurantId(), Restaurant.class);
        System.out.println(restaurant);
        return repository.save(booking);
    }

}
