package com.juniverse.hexagonal.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "User already exists";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String message = "User already exists";
        Throwable cause = new RuntimeException("Database constraint violation");
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testWithEmail() {
        String email = "test@example.com";
        UserAlreadyExistsException exception = UserAlreadyExistsException.withEmail(email);
        
        assertEquals("User already exists with email: test@example.com", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testWithNullEmail() {
        String email = null;
        UserAlreadyExistsException exception = UserAlreadyExistsException.withEmail(email);
        
        assertEquals("User already exists with email: null", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}