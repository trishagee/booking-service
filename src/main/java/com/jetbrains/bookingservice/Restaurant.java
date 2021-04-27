package com.jetbrains.bookingservice;

import java.util.StringJoiner;

public class Restaurant {
    public String id;
    public String name;

    @Override
    public String toString() {
        return new StringJoiner(", ", Restaurant.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
