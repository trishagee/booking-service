package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        //client selects a restaurant with an id and passes the id, date and time, number of diners, indoor vs outdoor
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

        Booking actualBooking = bookingById.get();
        // consider moving this into a helper method
        assertEquals(booking.getRestaurantId(), actualBooking.getRestaurantId());
        assertEquals(booking.getDate(), actualBooking.getDate());
        assertEquals(booking.getNumberOfDiners(), actualBooking.getNumberOfDiners());
    }

    @Test
    @DisplayName("Should not allow a booking on a day where existing bookings take up the capacity")
    @Disabled("not finished figuring out the correct response")
    void shouldNotAllowABookingOnADayWhereExistingBookingsTakeUpTheCapacity() throws Exception {
        // given:
        // three existing bookings in the database, 2 to nearly fill Dalia's restaurant
        Booking booking1 = new Booking("1", LocalDate.of(2021, 4, 26), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking("1", LocalDate.of(2021, 4, 26), 7);
        bookingRepository.save(booking2);

        // when
        Booking newBooking = new Booking("1", LocalDate.of(2021, 4, 26), 4);
        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                                                      .contentType("application/json")
                                                      .content(objectMapper.writeValueAsString(newBooking)))
                                     .andExpect(status().isOk())
                                     .andReturn();
        // currently throwing NoAvailableCapacityException, not sure what behaviour is expected at the http level

        fail("Not implemented");
    }

    // test - other restaurnat's capacities don't impact this one
    //

    // check restaurant capacity
    //  -  on that date
    //  -  for that capacity
    //  -  indoors vs outdoors
}
