package com.juniverse.hexagonal.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "User not found";
        UserNotFoundException exception = new UserNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String message = "User not found";
        Throwable cause = new RuntimeException("Database error");
        UserNotFoundException exception = new UserNotFoundException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testWithId() {
        Long userId = 123L;
        UserNotFoundException exception = UserNotFoundException.withId(userId);
        
        assertEquals("User not found with id: 123", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testWithNullId() {
        Long userId = null;
        UserNotFoundException exception = UserNotFoundException.withId(userId);
        
        assertEquals("User not found with id: null", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        UserNotFoundException exception = new UserNotFoundException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}