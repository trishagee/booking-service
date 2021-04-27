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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Test
    void shouldAskForTheRestaurantFromTheRestaurantService(@Mock BookingRepository repository,
                                                           @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);
        String restaurantId = "3";

        // when
        Booking newBooking = new Booking(restaurantId, LocalDateTime.now(), 10);
        bookingController.createBooking(newBooking, restTemplate);

        // then
        verify(restTemplate).getForObject("http://localhost:8080/restaurants/"+restaurantId, Restaurant.class);
    }

    @Test
    @DisplayName("Should check restaurant capacity")
    @Disabled("not finished")
    void shouldCheckRestaurantCapacity(@Mock BookingRepository repository,
                                       @Mock RestTemplate restTemplate) {
        // given
        BookingController bookingController = new BookingController(repository);
        // stub
        Mockito.when(restTemplate.getForObject(anyString(), eq(Restaurant.class)))
               .thenReturn(new Restaurant());

        // when
        Booking newBooking = new Booking("1", LocalDateTime.now(), 10);
        bookingController.createBooking(newBooking, restTemplate);

        // then
        verify(repository).save(newBooking);
        verifyNoMoreInteractions(repository, restTemplate);
    }
}