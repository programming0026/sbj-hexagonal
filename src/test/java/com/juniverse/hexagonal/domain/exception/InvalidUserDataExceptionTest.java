package com.juniverse.hexagonal.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidUserDataExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "Invalid user data";
        InvalidUserDataException exception = new InvalidUserDataException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String message = "Invalid user data";
        Throwable cause = new IllegalArgumentException("Invalid email format");
        InvalidUserDataException exception = new InvalidUserDataException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        InvalidUserDataException exception = new InvalidUserDataException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionWithEmptyMessage() {
        String message = "";
        InvalidUserDataException exception = new InvalidUserDataException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithNullMessage() {
        String message = null;
        InvalidUserDataException exception = new InvalidUserDataException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}