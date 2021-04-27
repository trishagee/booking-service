package com.jetbrains.bookingservice;

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
               .thenReturn(new Restaurant(restaurantId, 643728));

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
               .thenReturn(new Restaurant("2", 5));

        // expect
        Booking newBooking = new Booking("1", LocalDateTime.now(), 10);
        assertAll(() -> assertThrows(NoAvailableCapacityException.class,
                                     () -> bookingController.createBooking(newBooking, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }

    @Test
    @DisplayName("Should not allow a booking with more diners than availability for that day")
    @Disabled("Not implemented yet")
    void shouldNotAllowABookingWithMoreDinersThanAvailabilityForThatDay() {
        // for now, we're not going to worry about time / time slots, we going to do the stupidest thing and look at capacity for the whole day
        fail("Not implemented");
    }

    @Test
    @DisplayName("Should save a booking if the number of diners is fewer than the available capacity for the day")
    @Disabled("Not implemented yet")
    void shouldSaveABookingIfTheNumberOfDinersIsFewerThanTheAvailableCapacityForTheDay() {
        // for now, we're not going to worry about time / time slots, we going to do the stupidest thing and look at capacity for the whole day

        fail("Not implemented");
    }

    @Test
    @DisplayName("Should not allow a booking on a day the restaurant is shut")
    @Disabled("Not implemented yet")
    void shouldNotAllowABookingOnADayTheRestaurantIsShut() {
        // TODO: we haven't done anything around opening hours / days here yet
        fail("Not implemented");
    }
}