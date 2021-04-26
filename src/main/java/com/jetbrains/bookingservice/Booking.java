package com.jetbrains.bookingservice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private long id;
    private String restaurantId;
    private LocalDateTime dateTime;
    private int numberOfDiners;
    private Area area;

    public Booking() {

    }

    public Booking(final String restaurantId, final LocalDateTime dateTime, final int numberOfDiners, final Area area) {
        this.restaurantId = restaurantId;
        this.dateTime = dateTime;
        this.numberOfDiners = numberOfDiners;
        this.area = area;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getNumberOfDiners() {
        return numberOfDiners;
    }

    public Area getArea() {
        return area;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        if (id != booking.id) return false;
        if (numberOfDiners != booking.numberOfDiners) return false;
        if (!restaurantId.equals(booking.restaurantId)) return false;
        if (!dateTime.equals(booking.dateTime)) return false;
        return area == booking.area;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + restaurantId.hashCode();
        result = 31 * result + dateTime.hashCode();
        result = 31 * result + numberOfDiners;
        result = 31 * result + (area != null ? area.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Booking{");
        sb.append("id=").append(id);
        sb.append(", restaurantId='").append(restaurantId).append('\'');
        sb.append(", dateTime=").append(dateTime);
        sb.append(", numberOfDiners=").append(numberOfDiners);
        sb.append(", area=").append(area);
        sb.append('}');
        return sb.toString();
    }
}