package com.jetbrains.bookingservice;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestaurantClient {
    private final RestTemplate restTemplate;

    public RestaurantClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public Restaurant getRestaurant(String restaurantId) {
        // In a "real" environment this would at the very least be a property/environment variable, but ideally something like Service Discovery like Eureka
        return restTemplate.getForObject("http://localhost:8080/restaurants/" + restaurantId, Restaurant.class);
    }
}
