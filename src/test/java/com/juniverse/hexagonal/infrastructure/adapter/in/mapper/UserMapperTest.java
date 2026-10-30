package com.juniverse.hexagonal.infrastructure.adapter.in.mapper;

import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserCreateRequestDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserResponseDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserUpdateRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        testDateTime = LocalDateTime.now();
    }

    @Test
    void testToEntityFromCreateRequestDTO() {
        UserCreateRequestDTO dto = new UserCreateRequestDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("+1234567890");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("+1234567890", user.getPhone());
        assertNull(user.getId()); // Should not be set from create request
        assertNull(user.getCreatedAt()); // Should be set by JPA
        assertNull(user.getUpdatedAt()); // Should be set by JPA
    }

    @Test
    void testToEntityFromCreateRequestDTOWithNullPhone() {
        UserCreateRequestDTO dto = new UserCreateRequestDTO();
        dto.setName("Jane Doe");
        dto.setEmail("jane.doe@example.com");
        dto.setPhone(null);

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane.doe@example.com", user.getEmail());
        assertNull(user.getPhone());
    }

    @Test
    void testToEntityFromUpdateRequestDTO() {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("Updated Name");
        dto.setEmail("updated@example.com");
        dto.setPhone("+9876543210");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("Updated Name", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("+9876543210", user.getPhone());
        assertNull(user.getId()); // Should not be set from update request
        assertNull(user.getCreatedAt()); // Should be set by JPA
        assertNull(user.getUpdatedAt()); // Should be set by JPA
    }

    @Test
    void testToEntityFromUpdateRequestDTOWithNullPhone() {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setName("Updated User");
        dto.setEmail("updated@example.com");
        dto.setPhone(null);

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("Updated User", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertNull(user.getPhone());
    }

    @Test
    void testToResponseDTO() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("+1111111111");
        user.setCreatedAt(testDateTime);
        user.setUpdatedAt(testDateTime);

        UserResponseDTO dto = userMapper.toResponseDTO(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("+1111111111", dto.getPhone());
        assertEquals(testDateTime, dto.getCreatedAt());
        assertEquals(testDateTime, dto.getUpdatedAt());
    }

    @Test
    void testToResponseDTOWithNullPhone() {
        User user = new User();
        user.setId(2L);
        user.setName("User Without Phone");
        user.setEmail("nophone@example.com");
        user.setPhone(null);
        user.setCreatedAt(testDateTime);
        user.setUpdatedAt(testDateTime);

        UserResponseDTO dto = userMapper.toResponseDTO(user);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("User Without Phone", dto.getName());
        assertEquals("nophone@example.com", dto.getEmail());
        assertNull(dto.getPhone());
        assertEquals(testDateTime, dto.getCreatedAt());
        assertEquals(testDateTime, dto.getUpdatedAt());
    }

    @Test
    void testToResponseDTOList() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1.setPhone("+1111111111");
        user1.setCreatedAt(testDateTime);
        user1.setUpdatedAt(testDateTime);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2.setPhone("+2222222222");
        user2.setCreatedAt(testDateTime);
        user2.setUpdatedAt(testDateTime);

        List<User> users = Arrays.asList(user1, user2);
        List<UserResponseDTO> dtos = userMapper.toResponseDTOList(users);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        UserResponseDTO dto1 = dtos.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("User One", dto1.getName());
        assertEquals("user1@example.com", dto1.getEmail());

        UserResponseDTO dto2 = dtos.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("User Two", dto2.getName());
        assertEquals("user2@example.com", dto2.getEmail());
    }

    @Test
    void testToResponseDTOListWithEmptyList() {
        List<User> users = Arrays.asList();
        List<UserResponseDTO> dtos = userMapper.toResponseDTOList(users);

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void testToResponseDTOListWithNullList() {
        assertThrows(NullPointerException.class, () -> {
            userMapper.toResponseDTOList(null);
        });
    }

    @Test
    void testMappingPreservesDataIntegrity() {
        // Test that mapping doesn't lose or corrupt data
        UserCreateRequestDTO createDto = new UserCreateRequestDTO();
        createDto.setName("Integrity Test");
        createDto.setEmail("integrity@example.com");
        createDto.setPhone("+5555555555");

        User user = userMapper.toEntity(createDto);
        user.setId(100L);
        user.setCreatedAt(testDateTime);
        user.setUpdatedAt(testDateTime);

        UserResponseDTO responseDto = userMapper.toResponseDTO(user);

        assertEquals(createDto.getName(), responseDto.getName());
        assertEquals(createDto.getEmail(), responseDto.getEmail());
        assertEquals(createDto.getPhone(), responseDto.getPhone());
        assertEquals(100L, responseDto.getId());
        assertEquals(testDateTime, responseDto.getCreatedAt());
        assertEquals(testDateTime, responseDto.getUpdatedAt());
    }
}