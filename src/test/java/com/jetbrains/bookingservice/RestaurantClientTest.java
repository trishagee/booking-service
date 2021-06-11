package com.jetbrains.bookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Set;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest({RestaurantClient.class})
public class RestaurantClientTest {
    @Autowired
    private RestaurantClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    @DisplayName("Should call the restaurant service to get a restaurant by ID")
    void shouldCallTheRestaurantServiceToGetARestaurantById() throws Exception {
        // given
        String restaurantId = "43789432";
        Restaurant restaurant = new Restaurant(restaurantId, 9874378, Set.of());

        // stub response
        mockRestServiceServer
                .expect(once(), requestTo(startsWith("http://localhost:8080/restaurants/" + restaurantId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(objectMapper.writeValueAsString(restaurant)));

        // expect
        Restaurant actualRestaurant = client.getRestaurant(restaurantId);
        assertThat(actualRestaurant, Matchers.equalTo(restaurant));
    }
}
