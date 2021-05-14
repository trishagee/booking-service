package com.jetbrains.bookingservice.validation;

import com.jetbrains.bookingservice.Restaurant;
import com.jetbrains.bookingservice.RestaurantNotFoundException;
import org.springframework.lang.Nullable;

public class RestaurantValidator implements Validator<Restaurant> {
    @Override
    public void validate(@Nullable Restaurant restaurant) {
        if (restaurant == null) {
            throw new RestaurantNotFoundException();
        }
    }
}
