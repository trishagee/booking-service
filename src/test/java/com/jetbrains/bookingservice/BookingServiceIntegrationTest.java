package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.test.MySQLContainerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MySQLContainerExtension.class)
public class BookingServiceIntegrationTest {
    private static final String RESTAURANT_ID = "3";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private RestaurantClient restaurantClient;

    @BeforeEach
    public void setUp() {
        bookingRepository.deleteAll();

        Restaurant restaurant = new Restaurant(RESTAURANT_ID, 100, Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY));
        Mockito.when(restaurantClient.getRestaurant(anyString()))
               .thenReturn(restaurant);
    }

    @Test
    @DisplayName("Should be able to create a simple booking")
    void shouldBeAbleToCreateASimpleBooking() throws Exception {
        // given
        //client selects a restaurant with an ID and passes the ID, date and time, number of diners
        Booking booking = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 26), 4);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/restaurants/{restaurantId}/bookings", RESTAURANT_ID)
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
        Booking booking1 = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 26), 90);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 26), 7);
        bookingRepository.save(booking2);

        // when
        Booking newBooking = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 26), 4);
        mockMvc.perform(post("/restaurants/{restaurantId}/bookings", RESTAURANT_ID)
                       .contentType("application/json")
                       .content(objectMapper.writeValueAsString(newBooking)))
               .andExpect(status().isConflict())
               .andExpect(status().reason("NoAvailableCapacityException"));
    }

    @Test
    void shouldBeAbleToCreateABookingEvenWhenThereAreOtherBookingsOnADifferentDay() throws Exception {
        // existing bookings in the database, 2 to fill Helen's restaurant on a different day
        Booking booking1 = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 25), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 25), 19);
        bookingRepository.save(booking2);

        Booking booking = new Booking(RESTAURANT_ID, LocalDate.of(2021, 4, 24), 4);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/restaurants/{restaurantId}/bookings", RESTAURANT_ID)
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
        // existing bookings in the database, 2 to fill Trisha's restaurant
        Booking booking1 = new Booking("3", LocalDate.of(2021, 4, 27), 11);
        bookingRepository.save(booking1);
        Booking booking2 = new Booking("3", LocalDate.of(2021, 4, 27), 1);
        bookingRepository.save(booking2);

        String restaurantId = "4";
        Booking booking = new Booking(restaurantId, LocalDate.of(2021, 4, 27), 15);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/restaurants/{restaurantId}/bookings", restaurantId)
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
        bookingRepository.save(new Booking(RESTAURANT_ID, LocalDate.of(2021, 6, 12), 5));
        bookingRepository.save(new Booking(RESTAURANT_ID, LocalDate.of(2021, 6, 13), 7));
        bookingRepository.save(new Booking(RESTAURANT_ID, LocalDate.of(2021, 6, 14), 11));

        // when
        mockMvc.perform(get("/restaurants/{restaurantId}/bookings", RESTAURANT_ID))
               .andExpect(jsonPath("$[0].id").exists())
               .andExpect(jsonPath("$[0].restaurantId").value(RESTAURANT_ID))
               .andExpect(jsonPath("$[0].date").value("2021-06-12"))
               .andExpect(jsonPath("$[0].numberOfDiners").value(5))
               .andExpect(jsonPath("$[1].id").exists())
               .andExpect(jsonPath("$[1].restaurantId").value(RESTAURANT_ID))
               .andExpect(jsonPath("$[1].date").value("2021-06-13"))
               .andExpect(jsonPath("$[1].numberOfDiners").value(7))
               .andExpect(jsonPath("$[2].id").exists())
               .andExpect(jsonPath("$[2].restaurantId").value(RESTAURANT_ID))
               .andExpect(jsonPath("$[2].date").value("2021-06-14"))
               .andExpect(jsonPath("$[2].numberOfDiners").value(11));
    }

    @Test
    @DisplayName("Should get a list of bookings by date")
    void shouldGetAListOfBookingsByDate() throws Exception {
        // given
        bookingRepository.save(new Booking(RESTAURANT_ID, LocalDate.of(2021, 9, 12), 5));
        bookingRepository.save(new Booking(RESTAURANT_ID, LocalDate.of(2021, 9, 13), 7));
        bookingRepository.save(new Booking(RESTAURANT_ID, LocalDate.of(2021, 9, 14), 11));

        // when
        mockMvc.perform(get("/restaurants/{restaurantId}/bookings/2021-09-13", RESTAURANT_ID))
               .andExpect(jsonPath("$[0].id").exists())
               .andExpect(jsonPath("$[0].restaurantId").value(RESTAURANT_ID))
               .andExpect(jsonPath("$[0].date").value("2021-09-13"))
               .andExpect(jsonPath("$[0].numberOfDiners").value(7));
    }

    private void assertActualVsExpectedBooking(final Booking expected, final Booking actual) {
        assertEquals(expected.getRestaurantId(), actual.getRestaurantId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getNumberOfDiners(), actual.getNumberOfDiners());
    }

}
