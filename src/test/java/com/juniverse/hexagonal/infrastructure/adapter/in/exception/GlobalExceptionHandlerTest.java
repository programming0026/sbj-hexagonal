package com.juniverse.hexagonal.infrastructure.adapter.in.exception;

import com.juniverse.hexagonal.domain.exception.InvalidUserDataException;
import com.juniverse.hexagonal.domain.exception.UserAlreadyExistsException;
import com.juniverse.hexagonal.domain.exception.UserNotFoundException;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/users");
    }

    @Test
    void testHandleUserNotFoundException() {
        UserNotFoundException exception = UserNotFoundException.withId(123L);

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleUserNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("User not found with id: 123", errorResponse.getMessage());
        assertEquals("/api/v1/users", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testHandleUserAlreadyExistsException() {
        UserAlreadyExistsException exception = UserAlreadyExistsException.withEmail("test@example.com");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleUserAlreadyExistsException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(409, errorResponse.getStatus());
        assertEquals("Conflict", errorResponse.getError());
        assertEquals("User already exists with email: test@example.com", errorResponse.getMessage());
        assertEquals("/api/v1/users", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testHandleInvalidUserDataException() {
        InvalidUserDataException exception = new InvalidUserDataException("Invalid user data provided");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleInvalidUserDataException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals("Invalid user data provided", errorResponse.getMessage());
        assertEquals("/api/v1/users", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testHandleValidationExceptions() {
        FieldError fieldError1 = mock(FieldError.class);
        when(fieldError1.getField()).thenReturn("name");
        when(fieldError1.getDefaultMessage()).thenReturn("Name is required");

        FieldError fieldError2 = mock(FieldError.class);
        when(fieldError2.getField()).thenReturn("email");
        when(fieldError2.getDefaultMessage()).thenReturn("Email should be valid");

        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Input validation failed", errorResponse.getMessage());
        assertEquals("/api/v1/users", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNotNull(errorResponse.getDetails());
        assertEquals(2, errorResponse.getDetails().size());
        assertTrue(errorResponse.getDetails().contains("name: Name is required"));
        assertTrue(errorResponse.getDetails().contains("email: Email should be valid"));
    }

    @Test
    void testHandleValidationExceptionsWithEmptyFieldErrors() {
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Input validation failed", errorResponse.getMessage());
        assertNotNull(errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    void testHandleGenericException() {
        Exception exception = new Exception("Something went wrong");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertEquals("/api/v1/users", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testHandleGenericRuntimeException() {
        RuntimeException exception = new RuntimeException("Runtime error occurred");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponseDTO errorResponse = response.getBody();
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertEquals("/api/v1/users", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testTimestampIsRecentForAllExceptions() {
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);
        
        // Test UserNotFoundException
        UserNotFoundException userNotFoundException = UserNotFoundException.withId(1L);
        ResponseEntity<ErrorResponseDTO> response1 = globalExceptionHandler
                .handleUserNotFoundException(userNotFoundException, request);
        
        LocalDateTime afterTest = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(response1.getBody().getTimestamp().isAfter(beforeTest));
        assertTrue(response1.getBody().getTimestamp().isBefore(afterTest));

        // Test UserAlreadyExistsException
        UserAlreadyExistsException userAlreadyExistsException = UserAlreadyExistsException.withEmail("test@example.com");
        ResponseEntity<ErrorResponseDTO> response2 = globalExceptionHandler
                .handleUserAlreadyExistsException(userAlreadyExistsException, request);
        
        assertTrue(response2.getBody().getTimestamp().isAfter(beforeTest));
        assertTrue(response2.getBody().getTimestamp().isBefore(afterTest));

        // Test InvalidUserDataException
        InvalidUserDataException invalidUserDataException = new InvalidUserDataException("Invalid data");
        ResponseEntity<ErrorResponseDTO> response3 = globalExceptionHandler
                .handleInvalidUserDataException(invalidUserDataException, request);
        
        assertTrue(response3.getBody().getTimestamp().isAfter(beforeTest));
        assertTrue(response3.getBody().getTimestamp().isBefore(afterTest));

        // Test Generic Exception
        Exception genericException = new Exception("Generic error");
        ResponseEntity<ErrorResponseDTO> response4 = globalExceptionHandler
                .handleGenericException(genericException, request);
        
        assertTrue(response4.getBody().getTimestamp().isAfter(beforeTest));
        assertTrue(response4.getBody().getTimestamp().isBefore(afterTest));
    }

    @Test
    void testHandleExceptionWithNullMessage() {
        UserNotFoundException exception = new UserNotFoundException(null);

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleUserNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
    }

    @Test
    void testHandleUnreadableBodyReturns400() {
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleUnreadableBody(
                new org.springframework.http.converter.HttpMessageNotReadableException("bad json",
                        (org.springframework.http.HttpInputMessage) null), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed JSON request body", response.getBody().getMessage());
    }

    @Test
    void testHandleTypeMismatchReturns400() {
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex =
                new org.springframework.web.method.annotation.MethodArgumentTypeMismatchException(
                        "abc", Long.class, "id", null, new NumberFormatException());

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value for parameter 'id'", response.getBody().getMessage());
    }

    @Test
    void testHandleDataIntegrityReturns409() {
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDataIntegrity(
                new org.springframework.dao.DataIntegrityViolationException("Duplicate entry"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void testHandleExceptionWithDifferentRequestPaths() {
        when(request.getRequestURI()).thenReturn("/api/v1/users/123");
        
        UserNotFoundException exception = UserNotFoundException.withId(123L);

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler
                .handleUserNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("/api/v1/users/123", response.getBody().getPath());
    }
}