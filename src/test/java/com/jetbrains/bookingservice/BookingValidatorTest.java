package com.jetbrains.bookingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {
    private static final LocalDate date = LocalDate.of(2021, Month.SEPTEMBER, 18);

    @Test
    @DisplayName("Test validate should return not found error when restaurant was not present in DB")
    void testValidate_shouldReturnNotFoundErrorWhenRestaurantIsNotPresent(@Mock BookingRepository bookingRepository,
                                                                          @Mock RestaurantClient restaurantClient) {
      // Arrange
      BookingValidator bookingValidator = new BookingValidator(bookingRepository, restaurantClient);
      String restaurantId = "2";
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
      Mockito.when(restaurantClient.getRestaurant(anyString())).thenReturn(null);

      // Action
      BookingResponse bookingResponse = bookingValidator.validate(booking, restaurantId);

      // Assert
      assertEquals(HttpStatus.NOT_FOUND, bookingResponse.getHttpStatus());
      assertEquals(true, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test validate should return conflict error when number of diners in given booking exceed than capacity")
    void testValidate_shouldReturnErrorWhenDinersExceedCapacity(@Mock BookingRepository bookingRepository,
                                                                @Mock RestaurantClient restaurantClient) {
      // Arrange
      String restaurantId = "2";
      int numberOfDiners = 11;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      Mockito.when(restaurantClient.getRestaurant(anyString())).thenReturn(restaurant);
      BookingValidator bookingValidator = new BookingValidator(bookingRepository, restaurantClient);

      // Action
      BookingResponse bookingResponse = bookingValidator.validate(booking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CONFLICT, bookingResponse.getHttpStatus());
      assertEquals(true, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test validate should return conflict error when restaurant booking is not possible on Saturday")
    void testValidate_shouldErrorWhenBookingIsNotPossible(@Mock BookingRepository bookingRepository,
                                                          @Mock RestaurantClient restaurantClient) {
      // Arrange
      String restaurantId = "2";
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      Mockito.when(restaurantClient.getRestaurant(anyString())).thenReturn(restaurant);
      BookingValidator bookingValidator = new BookingValidator(bookingRepository, restaurantClient);

      // Action
      BookingResponse bookingResponse = bookingValidator.validate(booking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CONFLICT, bookingResponse.getHttpStatus());
      assertEquals(true, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test validate should return conflict error when total number of diners plus booking diners exceed than capacity")
    void testValidate_shouldReturnErrorWhenCapacityIsLess(@Mock BookingRepository bookingRepository,
                                                          @Mock RestaurantClient restaurantClient) {
      // Arrange
      String restaurantId = "2";
      int numberOfDiners = 5;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
      int capacity = 9;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      Mockito.when(restaurantClient.getRestaurant(anyString())).thenReturn(restaurant);
      Mockito.when(bookingRepository.findAllByRestaurantIdAndDate(anyString(),any()))
          .thenReturn(singletonList(booking));
      BookingValidator bookingValidator = new BookingValidator(bookingRepository, restaurantClient);

      // Action
      BookingResponse bookingResponse = bookingValidator.validate(booking, restaurantId);

      // Assert
      assertEquals(HttpStatus.CONFLICT, bookingResponse.getHttpStatus());
      assertEquals(true, bookingResponse.hasError());
    }

    @Test
    @DisplayName("Test validate should not return error when all validations are passed")
    void testValidate_shouldCreateBookingWhenAllValidationsArePassed(@Mock BookingRepository bookingRepository,
                                                                     @Mock RestaurantClient restaurantClient) {
      // Arrange
      String restaurantId = "2";
      int numberOfDiners = 4;
      Booking booking = new Booking(restaurantId, date, numberOfDiners);
      int capacity = 9;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      Mockito.when(restaurantClient.getRestaurant(anyString())).thenReturn(restaurant);
      Mockito.when(bookingRepository.findAllByRestaurantIdAndDate(anyString(),any()))
          .thenReturn(singletonList(booking));
      BookingValidator bookingValidator = new BookingValidator(bookingRepository, restaurantClient);

      // Action
      BookingResponse bookingResponse = bookingValidator.validate(booking, restaurantId);

      // Assert
      assertNull(bookingResponse.getHttpStatus());
      assertEquals(false, bookingResponse.hasError());
    }
}
