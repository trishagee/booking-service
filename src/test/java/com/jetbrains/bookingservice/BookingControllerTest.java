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
import java.util.List;
import java.util.Set;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Test
    @DisplayName("Should not allow bookings that exceed restaurant capacity")
    void shouldNotAllowBookingsThatExceedRestaurantCapacity(@Mock BookingRepository repository,
                                                            @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);
        // stub
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant("2", 5, Set.of()));

        // expect
        Booking newBooking = new Booking("1", LocalDateTime.now(), 10);
        assertAll(() -> assertThrows(NoAvailableCapacityException.class,
                                     () -> bookingController.createBooking(newBooking, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }

    @Test
    @DisplayName("Should throw an Exception if an invalid restaurant ID is given")
    void shouldThrowAnExceptionIfAnInvalidRestaurantIdIsGiven(@Mock BookingRepository repository,
                                                              @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);


        Booking newBooking = new Booking("1", LocalDateTime.now(), 5647);

        assertAll(() -> assertThrows(RestaurantNotFoundException.class,
                                     () -> bookingController.createBooking(newBooking, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }

    @Test
    @DisplayName("Should not allow a booking on a day the restaurant is shut")
    void shouldNotAllowABookingOnADayTheRestaurantIsShut(@Mock BookingRepository repository,
                                                         @Mock RestTemplate restTemplate) {
        // given:
        BookingController bookingController = new BookingController(repository);
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant("99", 20, Set.of(MONDAY, TUESDAY)));

        LocalDateTime bookingDateTime = LocalDateTime.of(2021, 4, 25, 18, 0);
        Booking newBooking = new Booking("99", bookingDateTime, 10);

        // expect:
        assertAll(() -> assertThrows(RestaurantClosedException.class,
                                     () -> bookingController.createBooking(newBooking, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }

    @Test
    @DisplayName("Should not allow a booking with more diners than availability for that day")
    void shouldNotAllowABookingWithMoreDinersThanAvailabilityForThatDay(@Mock BookingRepository repository,
                                                                        @Mock RestTemplate restTemplate) {
        // for now, we're not going to worry about time / time slots, we going to do the stupidest thing and look at capacity for the whole day
        // given:
        BookingController bookingController = new BookingController(repository);
        String restaurantId = "101";
        when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant(restaurantId, 20, Set.of(MONDAY, FRIDAY)));
        LocalDateTime bookingDateTime = LocalDateTime.of(2021, 4, 26, 18, 0);
        Booking newBooking = new Booking(restaurantId, bookingDateTime, 4);

        List<Booking> bookingList = List.of(new Booking(restaurantId, LocalDateTime.of(2021, 4, 26, 6, 0), 10),
                                            new Booking(restaurantId, LocalDateTime.of(2021, 4, 26, 11, 0), 7));
        // stub the response from the repository
        when(repository.findAllByRestaurantIdAndDateTime(restaurantId, bookingDateTime))
                .thenReturn(bookingList);

        // expect:
        assertAll(() -> assertThrows(NoAvailableCapacityException.class,
                                     () -> bookingController.createBooking(newBooking, restTemplate)),
                  () -> verifyNoMoreInteractions(repository));
    }

    @Test
    @DisplayName("Should save a booking if the number of diners is fewer than the available capacity for the day")
    @Disabled("Not implemented yet")
    void shouldSaveABookingIfTheNumberOfDinersIsFewerThanTheAvailableCapacityForTheDay() {
        // for now, we're not going to worry about time / time slots, we going to do the stupidest thing and look at capacity for the whole day
        // restaurant capacity: 20
        // number of diners: 4
        // existing bookings on that day: 15

        fail("Not implemented");
    }
}