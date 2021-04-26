package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.Repository;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

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
    @Disabled("Not implemented yet")
    void shouldBeAbleToCreateASimpleBooking() throws Exception {
        //client selects a restaurant with an id and passes the id, date and time, number of diners, indoor vs outdoor
        String restaurantId = "7";
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = new Booking(restaurantId, dateTime, 4, Area.INDOOR);

        // when
        mockMvc.perform(post("/bookings")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(booking)))
               .andExpect(status().isOk());

        // then
        Optional<Booking> actualBooking = bookingRepository.findById(booking.getId());
        Assertions.assertTrue(actualBooking.isPresent());
        Assertions.assertEquals(booking, actualBooking.get());
    }
}
