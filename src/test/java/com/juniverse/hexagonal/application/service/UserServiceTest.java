package com.juniverse.hexagonal.application.service;

import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.domain.exception.UserAlreadyExistsException;
import com.juniverse.hexagonal.domain.exception.UserNotFoundException;
import com.juniverse.hexagonal.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+1234567890");
        testUser.setCreatedAt(testDateTime);
        testUser.setUpdatedAt(testDateTime);
    }

    @Test
    void testCreateUserSuccess() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser.setPhone("+9876543210");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setName("New User");
        savedUser.setEmail("new@example.com");
        savedUser.setPhone("+9876543210");
        savedUser.setCreatedAt(testDateTime);
        savedUser.setUpdatedAt(testDateTime);

        when(userRepositoryPort.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New User", result.getName());
        assertEquals("new@example.com", result.getEmail());
        
        verify(userRepositoryPort).existsByEmail("new@example.com");
        verify(userRepositoryPort).save(newUser);
    }

    @Test
    void testCreateUserAlreadyExists() {
        User newUser = new User();
        newUser.setName("Existing User");
        newUser.setEmail("existing@example.com");

        when(userRepositoryPort.existsByEmail("existing@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(newUser);
        });

        assertEquals("User already exists with email: existing@example.com", exception.getMessage());
        verify(userRepositoryPort).existsByEmail("existing@example.com");
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        
        verify(userRepositoryPort).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepositoryPort).findById(999L);
    }

    @Test
    void testGetAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Second User");
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepositoryPort.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
        
        verify(userRepositoryPort).findAll();
    }

    @Test
    void testGetAllUsersEmpty() {
        when(userRepositoryPort.findAll()).thenReturn(Arrays.asList());

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(userRepositoryPort).findAll();
    }

    @Test
    void testUpdateUserSuccess() {
        User updateUser = new User();
        updateUser.setName("Updated User");
        updateUser.setEmail("updated@example.com");
        updateUser.setPhone("+1111111111");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Updated User");
        savedUser.setEmail("updated@example.com");
        savedUser.setPhone("+1111111111");
        savedUser.setCreatedAt(testDateTime);
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepositoryPort.existsById(1L)).thenReturn(true);
        when(userRepositoryPort.existsByEmailAndIdNot("updated@example.com", 1L)).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = userService.updateUser(1L, updateUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated User", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        
        verify(userRepositoryPort).existsById(1L);
        verify(userRepositoryPort).existsByEmailAndIdNot("updated@example.com", 1L);
        verify(userRepositoryPort).save(updateUser);
        assertEquals(1L, updateUser.getId()); // Verify ID was set
    }

    @Test
    void testUpdateUserNotFound() {
        User updateUser = new User();
        updateUser.setName("Updated User");
        updateUser.setEmail("updated@example.com");

        when(userRepositoryPort.existsById(999L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(999L, updateUser);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepositoryPort).existsById(999L);
        verify(userRepositoryPort, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserEmailAlreadyExists() {
        User updateUser = new User();
        updateUser.setName("Updated User");
        updateUser.setEmail("existing@example.com");

        when(userRepositoryPort.existsById(1L)).thenReturn(true);
        when(userRepositoryPort.existsByEmailAndIdNot("existing@example.com", 1L)).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(1L, updateUser);
        });

        assertEquals("User already exists with email: existing@example.com", exception.getMessage());
        verify(userRepositoryPort).existsById(1L);
        verify(userRepositoryPort).existsByEmailAndIdNot("existing@example.com", 1L);
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepositoryPort.existsById(1L)).thenReturn(true);
        doNothing().when(userRepositoryPort).deleteById(1L);

        assertDoesNotThrow(() -> {
            userService.deleteUser(1L);
        });

        verify(userRepositoryPort).existsById(1L);
        verify(userRepositoryPort).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepositoryPort.existsById(999L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepositoryPort).existsById(999L);
        verify(userRepositoryPort, never()).deleteById(anyLong());
    }

    @Test
    void testCreateUserWithNullEmail() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail(null);

        when(userRepositoryPort.existsByEmail(null)).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail(null);

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        verify(userRepositoryPort).existsByEmail(null);
        verify(userRepositoryPort).save(newUser);
    }

    @Test
    void testUpdateUserSameEmail() {
        // Test updating user with same email (should not trigger duplicate check)
        User updateUser = new User();
        updateUser.setName("Updated User");
        updateUser.setEmail("test@example.com"); // Same email as existing user

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Updated User");
        savedUser.setEmail("test@example.com");

        when(userRepositoryPort.existsById(1L)).thenReturn(true);
        when(userRepositoryPort.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = userService.updateUser(1L, updateUser);

        assertNotNull(result);
        verify(userRepositoryPort).existsById(1L);
        verify(userRepositoryPort).existsByEmailAndIdNot("test@example.com", 1L);
        verify(userRepositoryPort).save(updateUser);
    }
}