package com.jetbrains.bookingservice.views;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class BookingResponseView {
    private final Object data;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    public Object getData() {
      return data;
    }

    public HttpStatus getHttpStatus() {
      return httpStatus;
    }

  public BookingResponseView(String errorMessage, HttpStatus httpStatus) {
      this.data = null;
      this.errorMessage = errorMessage;
      this.httpStatus = httpStatus;
    }

    public BookingResponseView(Object data, String errorMessage, HttpStatus httpStatus) {
      this.data = data;
      this.errorMessage = errorMessage;
      this.httpStatus = httpStatus;
    }

    public Boolean hasError() {
      return errorMessage != null && !errorMessage.isEmpty();
    }

    public Boolean isErrorMessageEqualTo(String giveMessage){
      return Objects.equals(giveMessage, errorMessage);
    }
}
