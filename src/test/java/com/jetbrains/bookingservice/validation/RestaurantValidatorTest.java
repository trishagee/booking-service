package com.jetbrains.bookingservice.validation;

import com.jetbrains.bookingservice.RestaurantNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RestaurantValidatorTest {
    private final RestaurantValidator restaurantValidator = new RestaurantValidator();

    @Test
    @DisplayName("Should reject restaurants that are null")
    void shouldRejectRestaurantsThatAreNull() {
        assertThrows(RestaurantNotFoundException.class, () -> restaurantValidator.validate(null));
    }

}