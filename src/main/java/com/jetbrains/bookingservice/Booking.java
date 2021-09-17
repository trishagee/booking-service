package com.jetbrains.bookingservice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private Long id;
    private String restaurantId;
    private LocalDate date;
    private Integer numberOfDiners;

    public Booking() {}

    public Booking(final String restaurantId, final LocalDate date, final int numberOfDiners) {
        this.restaurantId = restaurantId;
        this.date = date;
        this.numberOfDiners = numberOfDiners;
    }

    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var booking = (Booking) o;

        if (!id.equals(booking.id)) return false;
        if (!numberOfDiners.equals(booking.numberOfDiners)) return false;
        if (!restaurantId.equals(booking.restaurantId)) return false;
        return date.equals(booking.date);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + restaurantId.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + numberOfDiners;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Booking{");
        sb.append("id=").append(id);
        sb.append(", restaurantId='").append(restaurantId).append('\'');
        sb.append(", date=").append(date);
        sb.append(", numberOfDiners=").append(numberOfDiners);
        sb.append('}');
        return sb.toString();
    }

    Boolean isNumberOfDinersMoreThanGivenCapacity(final Integer capacity){
        return this.numberOfDiners > capacity;
    }

    Boolean isPossibleOnGivenDateAt(final Restaurant restaurant) {
        return restaurant.isRestaurantOpenOn(this.date.getDayOfWeek());
    }

    Integer getSumOfBookingDinersAnd(final Integer totalDiners){
        return this.numberOfDiners + totalDiners;
    }

    static Integer calculateTotalDinersOnGivenDate(final List<Booking> bookings) {
        return bookings.stream()
            .mapToInt(booking -> booking.numberOfDiners)
            .sum();
    }
}
