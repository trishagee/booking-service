package com.jetbrains.bookingservice;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class BookingResponse {
    private final Object data;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    private static final String EMPTY_STRING = "";

    public BookingResponse(String errorMessage, HttpStatus httpStatus) {
      this.data = null;
      this.errorMessage = errorMessage;
      this.httpStatus = httpStatus;
    }

    public BookingResponse(Object data, String errorMessage, HttpStatus httpStatus) {
      this.data = data;
      this.errorMessage = errorMessage;
      this.httpStatus = httpStatus;
    }

    public Boolean hasError() {
      return !EMPTY_STRING.equals(errorMessage);
    }

    public HttpStatus getHttpStatus() {
      return httpStatus;
    }

    public Boolean isErrorMessageEqualTo(String giveMessage){
      return Objects.equals(giveMessage, errorMessage);
    }
}
