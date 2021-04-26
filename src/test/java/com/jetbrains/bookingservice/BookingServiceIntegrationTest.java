package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
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
        //client selects a restaurant with an id and passes the id, date and time, number of diners, indoor vs outdoor
        Booking booking = new Booking("7", LocalDateTime.now(), 4);

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
        assertEquals(booking.getDateTime(), actualBooking.getDateTime());
        assertEquals(booking.getNumberOfDiners(), actualBooking.getNumberOfDiners());
    }


    // check restaurant capacity
    //  -  on that date
    //  -  for that capacity
    //  -  indoors vs outdoors
}
