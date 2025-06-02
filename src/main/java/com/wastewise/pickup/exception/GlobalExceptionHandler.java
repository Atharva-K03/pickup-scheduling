// File: src/main/java/com/wastewise/pickup/exception/GlobalExceptionHandler.java
package com.wastewise.pickup.exception;

import com.wastewise.pickup.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Centralized exception handling across PickUp microservice.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PickUpNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(PickUpNotFoundException ex) {
        logger.error("PickUpNotFoundException: {}", ex.getMessage());
        return new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InvalidPickUpRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidRequest(InvalidPickUpRequestException ex) {
        logger.error("InvalidPickUpRequestException: {}", ex.getMessage());
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        logger.error("MethodArgumentNotValidException: {}", message);
        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                message, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleAll(Exception ex) {
        logger.error("Unhandled exception: ", ex);
        return new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred", LocalDateTime.now());
    }
}
