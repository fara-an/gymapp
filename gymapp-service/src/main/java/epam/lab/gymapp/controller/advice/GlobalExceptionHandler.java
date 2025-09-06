package epam.lab.gymapp.controller.advice;

import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<MessageResponse> handleDaoException(DaoException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(ex.getUserMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(ex.getUserMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(build(ex.getUserMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
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


    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build("It is not you it is us )"));
    }

    private MessageResponse build(String message) {
        return MessageResponse.builder()
                .message(message)
                .build();
    }

}
