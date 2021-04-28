package com.jetbrains.bookingservice;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    // TODO: this needs an integration test
    List<Booking> findAllByRestaurantIdAndDateTime(String restaurantId, LocalDateTime bookingDateTime);
}
