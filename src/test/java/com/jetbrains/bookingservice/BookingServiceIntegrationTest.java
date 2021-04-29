package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
        bookingRepository.deleteAll();
        //client selects a restaurant with an id and passes the id, date and time, number of diners
        Booking booking = new Booking("1", LocalDate.of(2021, 4, 26), 4);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                                                      .contentType("application/json")
                                                      .content(objectMapper.writeValueAsString(booking)))
                                     .andExpect(status().isOk())
                                     .andReturn();
        Booking returnedBooking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);

        // then
        Optional<Booking> bookingById = bookingRepository.findById(returnedBooking.getId());
        assertTrue(bookingById.isPresent());

        assertActualVsExpectedBooking(booking, bookingById.get());
    }

    @Test
    @DisplayName("Should not allow a booking on a day where existing bookings take up the capacity")
    void shouldNotAllowABookingOnADayWhereExistingBookingsTakeUpTheCapacity() throws Exception {
        // given:
        // existing bookings in the database, 2 to nearly fill Dalia's restaurant
        Booking booking1 = new Booking("1", LocalDate.of(2021, 4, 26), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking("1", LocalDate.of(2021, 4, 26), 7);
        bookingRepository.save(booking2);

        // when
        Booking newBooking = new Booking("1", LocalDate.of(2021, 4, 26), 4);
        mockMvc.perform(post("/bookings")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(newBooking)))
               .andExpect(status().isConflict())
               .andExpect(status().reason("NoAvailableCapacityException"));
    }

    @Test
    void shouldBeAbleToCreateABookingEvenWhenThereAreOtherBookingsOnADifferentDay() throws Exception {
        bookingRepository.deleteAll();
        // existing bookings in the database, 2 to fill Helen's restaurant on a different day
        Booking booking1 = new Booking("2", LocalDate.of(2021, 4, 25), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking("2", LocalDate.of(2021, 4, 25), 19);
        bookingRepository.save(booking2);

        Booking booking = new Booking("2", LocalDate.of(2021, 4, 24), 4);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                                                      .contentType("application/json")
                                                      .content(objectMapper.writeValueAsString(booking)))
                                     .andExpect(status().isOk())
                                     .andReturn();
        Booking returnedBooking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);

        // then
        Optional<Booking> bookingById = bookingRepository.findById(returnedBooking.getId());
        assertTrue(bookingById.isPresent());

        assertActualVsExpectedBooking(booking, bookingById.get());
    }

    @Test
    void shouldBeAbleToCreateABookingEvenWhenThereAreOtherBookingsAtADifferentRestaurant() throws Exception {
        bookingRepository.deleteAll();
        // existing bookings in the database, 2 to fill Trisha's restaurant
        Booking booking1 = new Booking("3", LocalDate.of(2021, 4, 27), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking("3", LocalDate.of(2021, 4, 27), 1);
        bookingRepository.save(booking2);

        Booking booking = new Booking("4", LocalDate.of(2021, 4, 27), 15);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                                                      .contentType("application/json")
                                                      .content(objectMapper.writeValueAsString(booking)))
                                     .andExpect(status().isOk())
                                     .andReturn();
        Booking returnedBooking = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Booking.class);

        // then
        Optional<Booking> bookingById = bookingRepository.findById(returnedBooking.getId());
        assertTrue(bookingById.isPresent());

        assertActualVsExpectedBooking(booking, bookingById.get());
    }

    private void assertActualVsExpectedBooking(final Booking expected, final Booking actual) {
        assertEquals(expected.getRestaurantId(), actual.getRestaurantId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getNumberOfDiners(), actual.getNumberOfDiners());
    }

}
