package com.example.demo.exception;

import com.example.demo.model.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class CustomerServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomerNotFoundException.class})
    ResponseEntity customerNotFoundHandler(Exception exception, ServletWebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.NOT_FOUND);
        apiError.setErrors(Arrays.asList(exception.getMessage()));
        apiError.setMessage(exception.getMessage());
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CustomerAlreadyExistsException.class})
    ResponseEntity customerAlreadyExistsHandler(Exception exception, ServletWebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.ALREADY_REPORTED);
        apiError.setErrors(Arrays.asList(exception.getMessage()));
        apiError.setMessage(exception.getMessage());
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler({CustomerNotActiveException.class})
    ResponseEntity customerNotActiveHandler(Exception exception, ServletWebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.EXPECTATION_FAILED);
        apiError.setErrors(Arrays.asList(exception.getMessage()));
        apiError.setMessage(exception.getMessage());
        apiError.setPath(request.getDescription(false));
        apiError.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.EXPECTATION_FAILED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField() + " : " + fieldError.getDefaultMessage());
        }

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setPath(request.getDescription(false));
        apiError.setErrors(errors);
        return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
    }
}
