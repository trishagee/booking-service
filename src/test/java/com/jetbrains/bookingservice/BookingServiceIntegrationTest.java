package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Should be able to create a simple booking")
    void shouldBeAbleToCreateASimpleBooking() throws Exception {
        //client selects a restaurant with an id and passes the id, date and time, number of diners
        int numberOfDiners = 4;
        Booking booking = createBooking("1", numberOfDiners);

        // when
        MvcResult mvcResult = postBookingAsJson(booking);
        Booking returnedBooking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);

        // then
        checkActualBookingMatchesExpected(booking, returnedBooking);
    }

    @Test
    @DisplayName("Should not allow a booking on a day where existing bookings take up the capacity")
    void shouldNotAllowABookingOnADayWhereExistingBookingsTakeUpTheCapacity() throws Exception {
        // given
        nearlyFillRestaurant("1");

        // expect
        var newBooking = createBooking("1", 4);
        postBookingAndExpectError(newBooking, status().isConflict(), "NoAvailableCapacityException");
    }

    @Test
    void shouldBeAbleToCreateABookingEvenWhenThereAreOtherBookingsOnADifferentDay() throws Exception {
        // given existing bookings in the database, 2 to fill restaurant on a different day
        insertBookings("2", 25, 19);

        Booking booking = createBooking("2", 4);

        // when
        MvcResult mvcResult = postBookingAsJson(booking);
        Booking returnedBooking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);

        // then
        checkActualBookingMatchesExpected(booking, returnedBooking);
    }

    @Test
    void shouldBeAbleToCreateABookingEvenWhenThereAreOtherBookingsAtADifferentRestaurant() throws Exception {
        bookingRepository.deleteAll();
        // existing bookings in the database, 2 to fill Trisha's restaurant
        insertBookings("3", 27, 1);

        Booking booking = createBooking("4", 15);

        // when
        MvcResult mvcResult = postBookingAsJson(booking);
        Booking returnedBooking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);

        // then
        checkActualBookingMatchesExpected(booking, returnedBooking);
    }

    private void checkActualBookingMatchesExpected(Booking booking, Booking returnedBooking) {
        Optional<Booking> bookingById = bookingRepository.findById(returnedBooking.getId());
        assertTrue(bookingById.isPresent());
        assertActualVsExpectedBooking(booking, bookingById.get());
    }

    private void postBookingAndExpectError(Booking newBooking, ResultMatcher status, String statusReason) throws Exception {
        mockMvc.perform(post("/bookings")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(newBooking)))
               .andExpect(status)
               .andExpect(status().reason(statusReason));
    }

    private MvcResult postBookingAsJson(Booking booking) throws Exception {
        return mockMvc.perform(post("/bookings")
                                       .contentType("application/json")
                                       .content(objectMapper.writeValueAsString(booking)))
                      .andExpect(status().isOk())
                      .andReturn();
    }

    private Booking createBooking(String restaurantId, int numberOfDiners) {
        return new Booking(restaurantId, LocalDate.of(2021, 4, 26), numberOfDiners);
    }

    private void nearlyFillRestaurant(String restaurantId) {
        insertBookings(restaurantId, 26, 7);
    }

    private void insertBookings(String restaurantId, int day, int numberOfDiners) {
        Booking booking1 = new Booking(restaurantId, LocalDate.of(2021, 4, day), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(restaurantId, LocalDate.of(2021, 4, day), numberOfDiners);
        bookingRepository.save(booking2);
    }

    private void assertActualVsExpectedBooking(final Booking expected, final Booking actual) {
        assertEquals(expected.getRestaurantId(), actual.getRestaurantId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getNumberOfDiners(), actual.getNumberOfDiners());
    }

}
