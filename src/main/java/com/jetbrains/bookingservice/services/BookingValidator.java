package com.jetbrains.bookingservice.services;

import com.jetbrains.bookingservice.repositories.BookingRepository;
import com.jetbrains.bookingservice.views.BookingResponseView;
import com.jetbrains.bookingservice.clients.RestaurantClient;
import com.jetbrains.bookingservice.models.Booking;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
public class BookingValidator {
    private final BookingRepository bookingRepository;
    private final RestaurantClient restaurantClient;

    public BookingValidator(BookingRepository bookingRepository, RestaurantClient restaurantClient) {
      this.bookingRepository = bookingRepository;
      this.restaurantClient = restaurantClient;
    }

    BookingResponseView validate(final Booking booking, final String restaurantId) {
      var restaurant = restaurantClient.getRestaurant(restaurantId);

      if (Objects.isNull(restaurant)) {
        return new BookingResponseView("Restaurant not found", HttpStatus.NOT_FOUND);
      }

      if (TRUE.equals(restaurant.isCapacityLessThanNumberOfDinersIn(booking))) {
        return new BookingResponseView("Number of diners exceeds available restaurant capacity",
            HttpStatus.CONFLICT);
      }

      if (FALSE.equals(booking.isPossibleOnGivenDateAt(restaurant))) {
        return new BookingResponseView("Restaurant is not open on", HttpStatus.CONFLICT);
      }

      Integer totalDinersOnThisDay = Booking.calculateTotalDinersOnGivenDate(bookingRepository
          .findAllByRestaurantIdAndDate(restaurantId, booking.getDate()));

      if (FALSE.equals(restaurant.isBookingPossibleForGiven(booking, totalDinersOnThisDay))) {
        return new BookingResponseView("Restaurant all booked up!", HttpStatus.CONFLICT);
      }

      return new BookingResponseView(booking, null, null);
    }
}
