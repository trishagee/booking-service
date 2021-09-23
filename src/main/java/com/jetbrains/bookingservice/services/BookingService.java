package com.jetbrains.bookingservice.services;

import com.jetbrains.bookingservice.repositories.BookingRepository;
import com.jetbrains.bookingservice.views.BookingResponseView;
import com.jetbrains.bookingservice.models.Booking;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;

    public BookingService(final BookingRepository bookingRepository, final BookingValidator bookingValidator) {
      this.bookingRepository = bookingRepository;
      this.bookingValidator = bookingValidator;
  }

    public BookingResponseView getBookingsForRestaurant(final String restaurantId) {
      List<Booking> bookings = bookingRepository.findAllByRestaurantId(restaurantId);

      if (bookings == null || bookings.isEmpty()) {
        return new BookingResponseView(bookings, "Bookings not found for a given restaurant", HttpStatus.NOT_FOUND);
      }
      return new BookingResponseView(bookings, null, HttpStatus.OK);
    }

    public BookingResponseView findAllByRestaurantIdAndDate(String restaurantId, LocalDate date) {
    List<Booking> bookings = bookingRepository.findAllByRestaurantIdAndDate(restaurantId, date);

    if (bookings == null || bookings.isEmpty()) {
      return new BookingResponseView(bookings, "Bookings not found for a given restaurant at given time", HttpStatus.NOT_FOUND);
    }

    return new BookingResponseView(bookings, null, HttpStatus.OK);
  }

    public BookingResponseView createBooking(final Booking booking, final String restaurantId) {
      var bookingResponse = bookingValidator.validate(booking, restaurantId);

      if (TRUE.equals(bookingResponse.hasError())) {
        return bookingResponse;
      }

      return new BookingResponseView(bookingRepository.save(booking), null, HttpStatus.CREATED);
    }
}
