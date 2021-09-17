package com.jetbrains.bookingservice;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
      return new BookingResponse(bookingRepository.findAllByRestaurantId(restaurantId), "", HttpStatus.OK);
    }

    public BookingResponse createBooking(final Booking booking, final String restaurantId) {
      var bookingResponse = bookingValidator.validate(booking, restaurantId);

      if (TRUE.equals(bookingResponse.hasError())) {
        return bookingResponse;
      }

      return new BookingResponse(bookingRepository.save(booking), "", HttpStatus.CREATED);
    }
}
