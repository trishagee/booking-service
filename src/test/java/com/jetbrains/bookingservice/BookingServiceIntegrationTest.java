package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.test.MySQLContainerExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MySQLContainerExtension.class)
public class BookingServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    //TODO: actually our API is wrong, restaurant/ID should be at the root
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

    @Test
    @DisplayName("Should get a list of bookings for a restaurant")
    void shouldGetAListOfBookingsForARestaurant() throws Exception {
        // given
        bookingRepository.deleteAll();
        String restaurantId = "3";
        bookingRepository.save(new Booking(restaurantId, LocalDate.of(2021, 6, 12), 5));
        bookingRepository.save(new Booking(restaurantId, LocalDate.of(2021, 6, 13), 7));
        bookingRepository.save(new Booking(restaurantId, LocalDate.of(2021, 6, 14), 11));

        // when
        mockMvc.perform(get("/restaurant/{restaurantId}/bookings", restaurantId))
               .andExpect(content().json("""
                                                 [{
                                                          "id" : 1,
                                                          "restaurantId" : "3",
                                                          "date" : "2021-06-12",
                                                          "numberOfDiners" : 5
                                                  },
                                                  {
                                                          "id" : 2,
                                                          "restaurantId" : "3",
                                                          "date" : "2021-06-13",
                                                          "numberOfDiners" : 7
                                                  },
                                                  {
                                                          "id" : 3,
                                                          "restaurantId" : "3",
                                                          "date" : "2021-06-14",
                                                          "numberOfDiners" : 11
                                                  }
                                                  ]
                                                 """));
    }

    private void assertActualVsExpectedBooking(final Booking expected, final Booking actual) {
        assertEquals(expected.getRestaurantId(), actual.getRestaurantId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getNumberOfDiners(), actual.getNumberOfDiners());
    }

}
