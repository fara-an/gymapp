package epam.lab.gymapp.controller.advice;

import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import epam.lab.gymapp.exceptions.UserInputException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERAL_EXCEPTION="Something went wrong on our end. Please try again";
    private static final String JWT_EXCEPTION="Something went wrong with your token. Please try again with a valid token";
    private static final String USER_INPUT_EXCEPTION="Problem with user input";
    private static final String ILLEGAL_ARGUMENT_EXCEPTION="User provided illegal argument";
    private static final String INVALID_CREDENTIALS_EXCEPTION="Invalid credentials provided";
    private static final String DAO_EXCEPTION="Error occurred during database interaction";
    private static final String ENTITY_NOT_EXCEPTION="Error occurred during database interaction";
    private static final String BAD_CREDENTIALS_EXCEPTION ="Bad credentials";

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<MessageResponse> handleDaoException(DaoException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(DAO_EXCEPTION));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(ENTITY_NOT_EXCEPTION));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(build(INVALID_CREDENTIALS_EXCEPTION));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(message));
    }

    @ExceptionHandler( MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParamException(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build("Request parameter "+ex.getParameterName()+ " is missing in the  Url"));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> handlePathVariableException(MissingPathVariableException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build("Request parameter "+ex.getVariableName()+" is missing in the Url"));
    }

    @ExceptionHandler(UserInputException.class)
    public ResponseEntity<MessageResponse> handleUserException(UserInputException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(build(USER_INPUT_EXCEPTION));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(ILLEGAL_ARGUMENT_EXCEPTION));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(BAD_CREDENTIALS_EXCEPTION));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<MessageResponse> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(build(JWT_EXCEPTION));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(GENERAL_EXCEPTION));
    }

    private MessageResponse build(String message) {
        return MessageResponse.builder()
                .message(message)
                .build();
    }

}
