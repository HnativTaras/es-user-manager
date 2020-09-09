package com.taras.hnativ.usermanager.controller.advice;

import com.taras.hnativ.usermanager.model.CustomErrorResponse;
import com.taras.hnativ.usermanager.controller.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleGenericNotFoundException(NotFoundException e) {

        CustomErrorResponse error = new CustomErrorResponse("NOT_FOUND_ERROR", e.getMessage());

        error.setTimestamp(LocalDateTime.now());

        error.setStatus((HttpStatus.NOT_FOUND.value()));

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

    }

}
