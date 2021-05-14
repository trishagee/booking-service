package com.jetbrains.bookingservice.validation;

public interface Validator<T> {
    void validate(T restaurant);
}
