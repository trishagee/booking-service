package com.jetbrains.bookingservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Test
    void shouldAskForTheRestaurantFromTheRestaurantService(@Mock BookingRepository repository,
                                                           @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);
        String restaurantId = "3";
        // stub the restaurant, because otherwise all the other logic will fall over.
        Mockito.when(restTemplate.getForObject("http://localhost:8080/restaurants/" + restaurantId, Restaurant.class))
               .thenReturn(new Restaurant(restaurantId, "Test", 643728));

        // when
        Booking newBooking = new Booking(restaurantId, LocalDateTime.now(), 10);
        bookingController.createBooking(newBooking, restTemplate);

        // then
        verify(restTemplate).getForObject("http://localhost:8080/restaurants/"+restaurantId, Restaurant.class);
    }

    @Test
    @DisplayName("Should not allow bookings that exceed restaurant capacity")
    void shouldNotAllowBookingsThatExceedRestaurantCapacity(@Mock BookingRepository repository,
                                                            @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);
        // stub
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant("2", "Test", 5));

        // expect
        Booking newBooking = new Booking("1", LocalDateTime.now(), 10);
        assertAll(() -> assertThrows(NoAvailableCapacityException.class,
                                     () -> bookingController.createBooking(newBooking, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }
}