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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        String restaurantId = "7";
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = new Booking(restaurantId, dateTime, 4, Area.INDOOR);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                                                      .contentType("application/json")
                                                      .content(objectMapper.writeValueAsString(booking)))
                                     .andExpect(status().isOk())
                                     .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        Booking booking1 = objectMapper.readValue(body, Booking.class);

        // then
        Optional<Booking> actualBooking = bookingRepository.findById(booking1.getId());
        Assertions.assertTrue(actualBooking.isPresent());
        Booking actual = actualBooking.get();
        // consider moving this into a helper method
        Assertions.assertEquals(booking.getRestaurantId(), actual.getRestaurantId());
        Assertions.assertEquals(booking.getDateTime(), actual.getDateTime());
        Assertions.assertEquals(booking.getArea(), actual.getArea());
        Assertions.assertEquals(booking.getNumberOfDiners(), actual.getNumberOfDiners());
    }
}
