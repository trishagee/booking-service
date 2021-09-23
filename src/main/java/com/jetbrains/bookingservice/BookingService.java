package com.jetbrains.bookingservice;

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

    public BookingResponse getBookingsForRestaurant(final String restaurantId) {
      List<Booking> bookings = bookingRepository.findAllByRestaurantId(restaurantId);

      if (bookings == null || bookings.isEmpty()) {
        return new BookingResponse(bookings, "Bookings not found for a given restaurant", HttpStatus.NOT_FOUND);
      }
      return new BookingResponse(bookings, null, HttpStatus.OK);
    }

    public BookingResponse findAllByRestaurantIdAndDate(String restaurantId, LocalDate date) {
    List<Booking> bookings = bookingRepository.findAllByRestaurantIdAndDate(restaurantId, date);

    if (bookings == null || bookings.isEmpty()) {
      return new BookingResponse(bookings, "Bookings not found for a given restaurant at given time", HttpStatus.NOT_FOUND);
    }

    return new BookingResponse(bookings, null, HttpStatus.OK);
  }

    public BookingResponse createBooking(final Booking booking, final String restaurantId) {
      var bookingResponse = bookingValidator.validate(booking, restaurantId);

      if (TRUE.equals(bookingResponse.hasError())) {
        return bookingResponse;
      }

      return new BookingResponse(bookingRepository.save(booking), null, HttpStatus.CREATED);
    }
}
