package com.jetbrains.bookingservice;

import java.time.DayOfWeek;
import java.util.Set;

public record Restaurant(String id, int capacity, Set<DayOfWeek> openingDays) {

}
