package com.juniverse.hexagonal.domain.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("+1234567890");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUserWithNullName() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testUserWithEmptyName() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testUserWithBlankName() {
        user.setName("   ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testUserWithShortName() {
        user.setName("A");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 50 characters")));
    }

    @Test
    void testUserWithLongName() {
        user.setName("A".repeat(51));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 50 characters")));
    }

    @Test
    void testUserWithNullEmail() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testUserWithEmptyEmail() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testUserWithInvalidEmail() {
        user.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    void testUserWithLongEmail() {
        user.setEmail("a".repeat(95) + "@test.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must not exceed 100 characters")));
    }

    @Test
    void testUserWithLongPhone() {
        user.setPhone("+123456789012345678");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone must not exceed 15 characters")));
    }

    @Test
    void testUserWithNullPhone() {
        user.setPhone(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty()); // Phone is optional
    }

    @Test
    void testUserConstructors() {
        // Test no-args constructor
        User emptyUser = new User();
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getName());
        assertNull(emptyUser.getEmail());

        // Test all-args constructor
        LocalDateTime now = LocalDateTime.now();
        User fullUser = new User(1L, "Jane Doe", "jane@example.com", "+1987654321", now, now);
        assertEquals(1L, fullUser.getId());
        assertEquals("Jane Doe", fullUser.getName());
        assertEquals("jane@example.com", fullUser.getEmail());
        assertEquals("+1987654321", fullUser.getPhone());
        assertEquals(now, fullUser.getCreatedAt());
        assertEquals(now, fullUser.getUpdatedAt());
    }

    @Test
    void testUserGettersAndSetters() {
        User testUser = new User();
        LocalDateTime now = LocalDateTime.now();

        testUser.setId(2L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+1111111111");
        testUser.setCreatedAt(now);
        testUser.setUpdatedAt(now);

        assertEquals(2L, testUser.getId());
        assertEquals("Test User", testUser.getName());
        assertEquals("test@example.com", testUser.getEmail());
        assertEquals("+1111111111", testUser.getPhone());
        assertEquals(now, testUser.getCreatedAt());
        assertEquals(now, testUser.getUpdatedAt());
    }

    @Test
    void testUserEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setName("John Doe");
        user2.setEmail("john@example.com");

        User user3 = new User();
        user3.setId(2L);
        user3.setName("Jane Doe");
        user3.setEmail("jane@example.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testUserToString() {
        String userString = user.toString();
        assertNotNull(userString);
        assertTrue(userString.contains("John Doe"));
        assertTrue(userString.contains("john.doe@example.com"));
    }
}