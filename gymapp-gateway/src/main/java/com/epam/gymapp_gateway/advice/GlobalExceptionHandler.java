package com.epam.gymapp_gateway.advice;

import com.epam.gymapp_gateway.dto.MessageResponse;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String GENERIC_ERROR_MESSAGE = "Something unexpected happened in Gymapp gateway service";
    private static final String  INTERNAL_GENERIC_ERROR_MESSAGE = "Error in  Gymapp gateway service {}";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception ex) {
        LOGGER.error(INTERNAL_GENERIC_ERROR_MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(GENERIC_ERROR_MESSAGE));
    }

    private MessageResponse build(String message) {
        return MessageResponse.builder()
                .message(message)
                .build();
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<MessageResponse> handleJwtException(JwtException jwtEx) {
        LOGGER.error(INTERNAL_GENERIC_ERROR_MESSAGE, jwtEx.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(GENERIC_ERROR_MESSAGE));
    }
}
