package com.jetbrains.bookingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        String restaurantId = "2";
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant(restaurantId, 5, Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)));

        // expect
        Booking newBooking = new Booking("1", LocalDate.now(), 10);
        assertAll(() -> assertThrows(NoAvailableCapacityException.class,
                                     () -> bookingController.createBooking(newBooking, restaurantId, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }

    @Test
    @DisplayName("Should throw an Exception if an invalid restaurant ID is given")
    void shouldThrowAnExceptionIfAnInvalidRestaurantIdIsGiven(@Mock BookingRepository repository,
                                                              @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);
        String restaurantId = "1";
        Booking newBooking = new Booking(restaurantId, LocalDate.now(), 5647);

        // expect
        assertAll(() -> assertThrows(RestaurantNotFoundException.class,
                                     () -> bookingController.createBooking(newBooking, restaurantId, restTemplate)),
                  () -> verifyNoInteractions(repository));
    }

    @Test
    @DisplayName("Should not allow a booking on a day the restaurant is shut")
    void shouldNotAllowABookingOnADayTheRestaurantIsShut(@Mock BookingRepository repository,
                                                         @Mock RestTemplate restTemplate) {
        // given:
        BookingController bookingController = new BookingController(repository);
        String restaurantId = "99";
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant(restaurantId, 20, Set.of(MONDAY, TUESDAY)));

        LocalDate bookingDate = LocalDate.of(2021, 4, 25);
        Booking newBooking = new Booking(restaurantId, bookingDate, 10);

        // expect:
        assertAll(() -> assertThrows(RestaurantClosedException.class,
                                     () -> bookingController.createBooking(newBooking, restaurantId, restTemplate)),
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
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant(restaurantId, 20, Set.of(MONDAY, FRIDAY)));
        LocalDate bookingDate = LocalDate.of(2021, 4, 26);
        Booking newBooking = new Booking(restaurantId, bookingDate, 4);

        List<Booking> bookingList = List.of(new Booking(restaurantId, LocalDate.of(2021, 4, 26), 10),
                                            new Booking(restaurantId, LocalDate.of(2021, 4, 26), 7));
        // stub the response from the repository
        Mockito.when(repository.findAllByRestaurantIdAndDate(restaurantId, bookingDate))
               .thenReturn(bookingList);

        // expect:
        assertAll(() -> assertThrows(NoAvailableCapacityException.class,
                                     () -> bookingController.createBooking(newBooking, restaurantId, restTemplate)),
                  () -> verifyNoMoreInteractions(repository));
    }

    @Test
    @DisplayName("Should save a booking if the number of diners is fewer than the available capacity for the day")
    void shouldSaveABookingIfTheNumberOfDinersIsFewerThanTheAvailableCapacityForTheDay(@Mock BookingRepository repository,
                                                                                       @Mock RestTemplate restTemplate) {
        // for now, we're not going to worry about time / time slots, we going to do the stupidest thing and look at capacity for the whole day
        BookingController bookingController = new BookingController(repository);
        String restaurantId = "101";
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant(restaurantId, 20, Set.of(MONDAY, FRIDAY)));
        LocalDate bookingDate = LocalDate.of(2021, 4, 26);
        Booking newBooking = new Booking(restaurantId, bookingDate, 4);

        List<Booking> bookingList = List.of(new Booking(restaurantId, LocalDate.of(2021, 4, 26), 8),
                                            new Booking(restaurantId, LocalDate.of(2021, 4, 26), 7));
        // stub the response from the repository
        Mockito.when(repository.findAllByRestaurantIdAndDate(restaurantId, bookingDate))
               .thenReturn(bookingList);

        // when:
        bookingController.createBooking(newBooking, restaurantId, restTemplate);

        // expect:
        verify(repository).save(newBooking);
    }
}