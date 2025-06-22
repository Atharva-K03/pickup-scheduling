package com.wastewise.pickup.exception;

import com.wastewise.pickup.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Focuses on converting exceptions to meaningful API error responses.
 * Catches application-specific exceptions (e.g., {@link PickUpNotFoundException}, {@link InvalidPickUpRequestException})
 * and returns appropriate HTTP responses. Logs all handled exceptions.
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the {@link PickUpNotFoundException}.
     * This exception is thrown when a requested pick-up is not found in the database.
     *
     * @param ex The {@link PickUpNotFoundException} that was thrown.
     * @return An {@link ApiErrorResponse} indicating that the requested pick-up was not found.
     */
    @ExceptionHandler(PickUpNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(PickUpNotFoundException ex) {
        log.error("PickUpNotFoundException: {}", ex.getMessage());
        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    /**
     * Handles the {@link InvalidPickUpRequestException}.
     * This exception is thrown when the user provides invalid input data to the PickUp API.
     *
     * @param ex The {@link InvalidPickUpRequestException} that was thrown.
     * @return An {@link ApiErrorResponse} indicating that the input provided was invalid.
     */
    @ExceptionHandler(InvalidPickUpRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidRequest(InvalidPickUpRequestException ex) {
        log.error("InvalidPickUpRequestException: {}", ex.getMessage());
        return new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    /**
     * Handles the {@link MethodArgumentNotValidException}.
     * This exception occurs when the input validation using annotations
     * (e.g., @NotNull, @Min, @Max) fails during request processing.
     *
     * @param ex The {@link MethodArgumentNotValidException} that was thrown.
     * @return An {@link ApiErrorResponse} containing details of the validation error.
     *         The response focuses on the first validation error encountered.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        // Extract the first validation error message
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.error("MethodArgumentNotValidException: {}", message);

        return new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );
    }

    /**
     * Handles all unhandled exceptions in the application.
     * response with HTTP status 500 (Internal Server Error) and a generic error message.
     *
     * @param ex The exception that was thrown.
     * @return An {@link ApiErrorResponse} indicating an internal error occurred.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleAll(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now()
        );
    }
}