package epam.lab.gymapp.controller.advice;

import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MissingServletRequestParameterException missingServletRequestParameterException;

    @Mock
    private MissingPathVariableException missingPathVariableException;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleDaoException_ShouldReturnInternalServerError() {
        String errorMessage = "Error occurred during database interaction.";
        DaoException daoException = new DaoException(errorMessage);

        ResponseEntity<MessageResponse> response = globalExceptionHandler.handleDaoException(daoException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFound() {
        String errorMessage = "Requested entity  was not found";
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException(errorMessage);

        ResponseEntity<?> response = globalExceptionHandler.handleEntityNotFoundException(entityNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals(errorMessage, ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleInvalidCredentialsException_ShouldReturnUnauthorized() {
        String errorMessage = "Provided credentials are not valid";
        InvalidCredentialsException invalidCredentialsException = new InvalidCredentialsException(errorMessage);

        ResponseEntity<?> response = globalExceptionHandler.handleInvalidCredentialsException(invalidCredentialsException);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals(errorMessage, ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest_WithSingleFieldError() {
        FieldError fieldError = new FieldError("user", "email", "must be a valid email");
        List<FieldError> fieldErrors = Collections.singletonList(fieldError);

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<?> response = globalExceptionHandler.handleException(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("email must be a valid email", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest_WithMultipleFieldErrors() {
        FieldError fieldError1 = new FieldError("user", "email", "must be a valid email");
        FieldError fieldError2 = new FieldError("user", "name", "must not be blank");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<?> response = globalExceptionHandler.handleException(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("email must be a valid email;name must not be blank",
                ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest_WithEmptyFieldErrors() {
        List<FieldError> fieldErrors = Collections.emptyList();

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<?> response = globalExceptionHandler.handleException(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturnBadRequest() {
        String parameterName = "userId";
        when(missingServletRequestParameterException.getParameterName()).thenReturn(parameterName);

        ResponseEntity<?> response = globalExceptionHandler.handleMissingParamException(missingServletRequestParameterException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("Request parameter userId is missing in the  Url",
                ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMissingPathVariableException_ShouldReturnBadRequest() {
        String variableName = "id";
        when(missingPathVariableException.getVariableName()).thenReturn(variableName);

        ResponseEntity<?> response = globalExceptionHandler.handlePathVariableException(missingPathVariableException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("Request parameter id is missing in the Url",
                ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleGenericException_ShouldReturnBadRequest() {
        ResponseEntity<MessageResponse> response = globalExceptionHandler.handleException(new Exception());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("It is not you it is us )", response.getBody().getMessage());
    }

    @Test
    void build_ShouldCreateMessageResponseWithCorrectMessage() {
        String testMessage = "Error occurred during database interaction.";

        DaoException daoException = new DaoException(testMessage);
        ResponseEntity<MessageResponse> response = globalExceptionHandler.handleDaoException(daoException);

        assertNotNull(response.getBody());
        assertEquals(testMessage, response.getBody().getMessage());
    }

    @Test
    void handleDaoException_WithNullMessage_ShouldReturnResponseWithNullMessage() {
        String message = "Error occurred during database interaction.";
        DaoException daoException = new DaoException(message);

        ResponseEntity<MessageResponse> response = globalExceptionHandler.handleDaoException(daoException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull((response.getBody().getMessage()));
    }

    @Test
    void handleEntityNotFoundException_WithEmptyMessage_ShouldReturnResponseWithEmptyMessage() {
        String message = "Requested entity  was not found";
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException(message);

        ResponseEntity<?> response = globalExceptionHandler.handleEntityNotFoundException(entityNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals(message, ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleInvalidCredentialsException_WithLongMessage_ShouldReturnResponseWithFullMessage() {
        String longMessage = "Provided credentials are not valid";
        InvalidCredentialsException invalidCredentialsException = new InvalidCredentialsException(longMessage);

        ResponseEntity<?> response = globalExceptionHandler.handleInvalidCredentialsException(invalidCredentialsException);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals(longMessage, ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMissingServletRequestParameterException_WithNullParameterName_ShouldHandleGracefully() {
        when(missingServletRequestParameterException.getParameterName()).thenReturn(null);

        ResponseEntity<?> response = globalExceptionHandler.handleMissingParamException(missingServletRequestParameterException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("Request parameter null is missing in the  Url",
                ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleMissingPathVariableException_WithNullVariableName_ShouldHandleGracefully() {
        when(missingPathVariableException.getVariableName()).thenReturn(null);

        ResponseEntity<?> response = globalExceptionHandler.handlePathVariableException(missingPathVariableException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("Request parameter null is missing in the Url",
                ((MessageResponse) response.getBody()).getMessage());
    }
}