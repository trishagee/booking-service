package com.jetbrains.bookingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingTest {
    @Test
    @DisplayName("Test isNumberOfDinersMoreThanGivenCapacity should return false when number of diners are less than given capacity")
    void testIsNumberOfDinersMoreThanGivenCapacity_shouldReturnFalseWhenNumberOfDinersAreLess() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

      // Action & Assert
      assertEquals(false, booking.isNumberOfDinersMoreThanGivenCapacity(12));
    }

    @Test
    @DisplayName("Test isNumberOfDinersMoreThanGivenCapacity should return true when number of diners are more than given capacity")
    void testIsNumberOfDinersMoreThanGivenCapacity_shouldReturnTrueWhenNumberOfDinersAreMore() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

      // Action & Assert
      assertEquals(true, booking.isNumberOfDinersMoreThanGivenCapacity(9));
    }

    @Test
    @DisplayName("Test isNumberOfDinersMoreThanGivenCapacity should return true when booking is possible on any day")
    void testIsPossibleOnGivenDateAt_shouldReturnTrueWhenBookingIsPossibleOnAnyDay() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      Restaurant restaurant = new Restaurant(restaurantId, 5, Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY,
          FRIDAY, SATURDAY, SUNDAY));

      // Action & Assert
      assertEquals(true, booking.isPossibleOnGivenDateAt(restaurant));
    }

    @Test
    @DisplayName("Test IsPossibleOnGivenDateAt should return false when booking is not possible on saturday")
    void testIsPossibleOnGivenDateAt_shouldReturnFalseWhenBookingIsNotPossibleOnSaturday() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.of(2021, 9, 18);
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      Restaurant restaurant = new Restaurant(restaurantId, 5, Set.of(TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SUNDAY));

      // Action & Assert
      assertEquals(false, booking.isPossibleOnGivenDateAt(restaurant));
    }

    @Test
    @DisplayName("Test getSumOfBookingDinersAnd should return sum of totalDiners in all bookings & diners in given booking")
    void testGetSumOfBookingDinersAnd_shouldReturnCorrectSum() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 10;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);

      // Action & Assert
      assertEquals(20, booking.getSumOfBookingDinersAnd(10));
    }

    @Test
    @DisplayName("Test calculateTotalDinersOnGivenDate should return totalDiners in all bookings")
    void testCalculateTotalDinersOnGivenDate_shouldReturnTotalDiners() {
      // Arrange
      String restaurantId = "2";
      LocalDate now = LocalDate.now();
      int numberOfDiners = 11;
      Booking booking = new Booking(restaurantId, now, numberOfDiners);
      Booking anotherBooking = new Booking(restaurantId, now, numberOfDiners);


      // Action & Assert
      assertEquals(22, Booking.calculateTotalDinersOnGivenDate(asList(booking, anotherBooking)));
    }
}