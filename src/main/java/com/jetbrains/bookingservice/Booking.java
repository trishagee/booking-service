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

    public Booking(final String restaurantId, final LocalDateTime dateTime, final int numberOfDiners, final Area area) {
        this.restaurantId = restaurantId;
        this.dateTime = dateTime;
        this.numberOfDiners = numberOfDiners;
        this.area = area;
    }

    public Booking() {

    }

    public Long getId() {
        return id;
    }

}