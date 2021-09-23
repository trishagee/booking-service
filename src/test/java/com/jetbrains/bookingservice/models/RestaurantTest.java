package com.jetbrains.bookingservice.models;

import com.jetbrains.bookingservice.models.Booking;
import com.jetbrains.bookingservice.models.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RestaurantTest {
    @Test
    @DisplayName("Test isCapacityLessThanNumberOfDinersIn should return false when number of diners does not exceed capacity")
    void testIsCapacityLessThanNumberOfDinersIn_shouldReturnFalseWhenCapacityNotExceedsNumberOfDiners() {
    // Arrange
      String restaurantId = "2";
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      LocalDate now = LocalDate.now();
      int numberOfDiners = 2;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

    // Action & Assert
    assertEquals(false, restaurant.isCapacityLessThanNumberOfDinersIn(booking));
  }

    @Test
    @DisplayName("Test isCapacityLessThanNumberOfDinersIn should return true when number of diners exceeds capacity")
    void testIsCapacityLessThanNumberOfDinersIn_shouldReturnTrueWhenCapacityExceedsNumberOfDiners() {
      // Arrange
      String restaurantId = "2";
      int capacity = 1;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      LocalDate now = LocalDate.now();
      int numberOfDiners = 20;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

      // Action & Assert
      assertEquals(true, restaurant.isCapacityLessThanNumberOfDinersIn(booking));
    }

    @Test
    @DisplayName("Test isRestaurantOpenOn should return true when restaurant is open all days of week")
    void testIsRestaurantOpenOn_shouldReturnTrueWhenRestaurantIsOpen() {
      // Arrange
      String restaurantId = "2";
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);

      // Action & Assert
      assertEquals(true, restaurant.isRestaurantOpenOn(MONDAY));
    }

    @Test
    @DisplayName("Test isRestaurantOpenOn should return false when restaurant is closed on monday")
    void testIsRestaurantOpenOn_shouldReturnFalseWhenRestaurantIsClosed() {
      // Arrange
      String restaurantId = "2";
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);

      // Action & Assert
      assertEquals(false, restaurant.isRestaurantOpenOn(MONDAY));
    }

    @Test
    @DisplayName("Test isBookingPossibleForGiven should return true when booking is possible")
    void testIsBookingPossibleForGiven_shouldReturnTrue() {
      // Arrange
      String restaurantId = "2";
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      LocalDate now = LocalDate.now();
      int numberOfDiners = 2;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

      // Action & Assert
      assertEquals(true, restaurant.isBookingPossibleForGiven(booking, 8));
    }

    @Test
    @DisplayName("Test isBookingPossibleForGiven should return false when booking is not possible")
    void testIsBookingPossibleForGiven_shouldReturnFalse() {
      // Arrange
      String restaurantId = "2";
      int capacity = 10;
      Set<DayOfWeek> dayOfWeeks = Set.of(TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
      Restaurant restaurant = new Restaurant(restaurantId, capacity, dayOfWeeks);
      LocalDate now = LocalDate.now();
      int numberOfDiners = 2;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

      // Action & Assert
      assertEquals(false, restaurant.isBookingPossibleForGiven(booking, 10));
    }
}