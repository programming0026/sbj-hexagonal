package com.juniverse.hexagonal.infrastructure.adapter.out;

import com.juniverse.hexagonal.domain.entity.User;
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
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+1234567890");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSave() {
        when(userJpaRepository.save(any(User.class))).thenReturn(testUser);

        User result = userRepositoryAdapter.save(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userJpaRepository).save(testUser);
    }

    @Test
    void testFindById() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userRepositoryAdapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getName(), result.get().getName());
        verify(userJpaRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userRepositoryAdapter.findById(1L);

        assertFalse(result.isPresent());
        verify(userJpaRepository).findById(1L);
    }

    @Test
    void testFindAll() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Second User");
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(testUser, user2);
        when(userJpaRepository.findAll()).thenReturn(users);

        List<User> result = userRepositoryAdapter.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
        verify(userJpaRepository).findAll();
    }

    @Test
    void testFindAllEmpty() {
        when(userJpaRepository.findAll()).thenReturn(Arrays.asList());

        List<User> result = userRepositoryAdapter.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userJpaRepository).findAll();
    }

    @Test
    void testExistsByEmail() {
        String email = "test@example.com";
        when(userJpaRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userRepositoryAdapter.existsByEmail(email);

        assertTrue(result);
        verify(userJpaRepository).existsByEmail(email);
    }

    @Test
    void testExistsByEmailNotFound() {
        String email = "notfound@example.com";
        when(userJpaRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userRepositoryAdapter.existsByEmail(email);

        assertFalse(result);
        verify(userJpaRepository).existsByEmail(email);
    }

    @Test
    void testExistsByEmailAndIdNot() {
        String email = "test@example.com";
        Long id = 1L;
        when(userJpaRepository.existsByEmailAndIdNot(email, id)).thenReturn(false);

        boolean result = userRepositoryAdapter.existsByEmailAndIdNot(email, id);

        assertFalse(result);
        verify(userJpaRepository).existsByEmailAndIdNot(email, id);
    }

    @Test
    void testExistsByEmailAndIdNotFound() {
        String email = "test@example.com";
        Long id = 2L;
        when(userJpaRepository.existsByEmailAndIdNot(email, id)).thenReturn(true);

        boolean result = userRepositoryAdapter.existsByEmailAndIdNot(email, id);

        assertTrue(result);
        verify(userJpaRepository).existsByEmailAndIdNot(email, id);
    }

    @Test
    void testDeleteById() {
        doNothing().when(userJpaRepository).deleteById(1L);

        userRepositoryAdapter.deleteById(1L);

        verify(userJpaRepository).deleteById(1L);
    }

    @Test
    void testExistsById() {
        when(userJpaRepository.existsById(1L)).thenReturn(true);

        boolean result = userRepositoryAdapter.existsById(1L);

        assertTrue(result);
        verify(userJpaRepository).existsById(1L);
    }

    @Test
    void testExistsByIdNotFound() {
        when(userJpaRepository.existsById(999L)).thenReturn(false);

        boolean result = userRepositoryAdapter.existsById(999L);

        assertFalse(result);
        verify(userJpaRepository).existsById(999L);
    }

    @Test
    void testSaveWithNullUser() {
        when(userJpaRepository.save(null)).thenThrow(new IllegalArgumentException("User cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            userRepositoryAdapter.save(null);
        });

        verify(userJpaRepository).save(null);
    }

    @Test
    void testFindByIdWithNullId() {
        when(userJpaRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            userRepositoryAdapter.findById(null);
        });

        verify(userJpaRepository).findById(null);
    }

    @Test
    void testExistsByEmailWithNullEmail() {
        when(userJpaRepository.existsByEmail(null)).thenReturn(false);

        boolean result = userRepositoryAdapter.existsByEmail(null);

        assertFalse(result);
        verify(userJpaRepository).existsByEmail(null);
    }

    @Test
    void testExistsByEmailAndIdNotWithNullEmail() {
        when(userJpaRepository.existsByEmailAndIdNot(null, 1L)).thenReturn(false);

        boolean result = userRepositoryAdapter.existsByEmailAndIdNot(null, 1L);

        assertFalse(result);
        verify(userJpaRepository).existsByEmailAndIdNot(null, 1L);
    }
}