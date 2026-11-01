package com.cours_m2.gastroguidemichelin.config.exception;

import com.cours_m2.gastroguidemichelin.restaurant.exception.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ProblemDetail handleRestaurantNotFoundException(RestaurantNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Restaurant introuvable");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}
