package com.juniverse.hexagonal.infrastructure.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juniverse.hexagonal.domain.entity.User;
import com.juniverse.hexagonal.domain.exception.UserAlreadyExistsException;
import com.juniverse.hexagonal.domain.exception.UserNotFoundException;
import com.juniverse.hexagonal.domain.port.in.UserServicePort;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserCreateRequestDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserResponseDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.dto.UserUpdateRequestDTO;
import com.juniverse.hexagonal.infrastructure.adapter.in.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServicePort userServicePort;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserCreateRequestDTO createRequestDTO;
    private UserUpdateRequestDTO updateRequestDTO;
    private UserResponseDTO responseDTO;
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

        createRequestDTO = new UserCreateRequestDTO();
        createRequestDTO.setName("Test User");
        createRequestDTO.setEmail("test@example.com");
        createRequestDTO.setPhone("+1234567890");

        updateRequestDTO = new UserUpdateRequestDTO();
        updateRequestDTO.setName("Updated User");
        updateRequestDTO.setEmail("updated@example.com");
        updateRequestDTO.setPhone("+9876543210");

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .phone("+1234567890")
                .createdAt(testDateTime)
                .updatedAt(testDateTime)
                .build();
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        when(userMapper.toEntity(any(UserCreateRequestDTO.class))).thenReturn(testUser);
        when(userServicePort.createUser(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"));

        verify(userMapper).toEntity(any(UserCreateRequestDTO.class));
        verify(userServicePort).createUser(any(User.class));
        verify(userMapper).toResponseDTO(any(User.class));
    }

    @Test
    void testCreateUserValidationError() throws Exception {
        UserCreateRequestDTO invalidRequest = new UserCreateRequestDTO();
        invalidRequest.setName(""); // Invalid - blank name
        invalidRequest.setEmail("invalid-email"); // Invalid - bad email format
        invalidRequest.setPhone("+123456789012345678"); // Invalid - too long

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userMapper, never()).toEntity(any(UserCreateRequestDTO.class));
        verify(userServicePort, never()).createUser(any(User.class));
    }

    @Test
    void testCreateUserAlreadyExists() throws Exception {
        when(userMapper.toEntity(any(UserCreateRequestDTO.class))).thenReturn(testUser);
        when(userServicePort.createUser(any(User.class)))
                .thenThrow(UserAlreadyExistsException.withEmail("test@example.com"));

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isConflict());

        verify(userMapper).toEntity(any(UserCreateRequestDTO.class));
        verify(userServicePort).createUser(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() throws Exception {
        when(userServicePort.getUserById(1L)).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(responseDTO);

        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userServicePort).getUserById(1L);
        verify(userMapper).toResponseDTO(testUser);
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(userServicePort.getUserById(999L)).thenThrow(UserNotFoundException.withId(999L));

        mockMvc.perform(get("/v1/users/999"))
                .andExpect(status().isNotFound());

        verify(userServicePort).getUserById(999L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Second User");
        user2.setEmail("user2@example.com");

        UserResponseDTO responseDTO2 = UserResponseDTO.builder()
                .id(2L)
                .name("Second User")
                .email("user2@example.com")
                .build();

        List<User> users = Arrays.asList(testUser, user2);
        List<UserResponseDTO> responseDTOs = Arrays.asList(responseDTO, responseDTO2);

        when(userServicePort.getAllUsers()).thenReturn(users);
        when(userMapper.toResponseDTOList(users)).thenReturn(responseDTOs);

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Second User"));

        verify(userServicePort).getAllUsers();
        verify(userMapper).toResponseDTOList(users);
    }

    @Test
    void testGetAllUsersEmpty() throws Exception {
        when(userServicePort.getAllUsers()).thenReturn(Arrays.asList());
        when(userMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userServicePort).getAllUsers();
        verify(userMapper).toResponseDTOList(anyList());
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPhone("+9876543210");

        UserResponseDTO updatedResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .name("Updated User")
                .email("updated@example.com")
                .phone("+9876543210")
                .build();

        when(userMapper.toEntity(any(UserUpdateRequestDTO.class))).thenReturn(updatedUser);
        when(userServicePort.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDTO(updatedUser)).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userMapper).toEntity(any(UserUpdateRequestDTO.class));
        verify(userServicePort).updateUser(eq(1L), any(User.class));
        verify(userMapper).toResponseDTO(updatedUser);
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        when(userMapper.toEntity(any(UserUpdateRequestDTO.class))).thenReturn(testUser);
        when(userServicePort.updateUser(eq(999L), any(User.class)))
                .thenThrow(UserNotFoundException.withId(999L));

        mockMvc.perform(put("/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isNotFound());

        verify(userMapper).toEntity(any(UserUpdateRequestDTO.class));
        verify(userServicePort).updateUser(eq(999L), any(User.class));
    }

    @Test
    void testUpdateUserEmailAlreadyExists() throws Exception {
        when(userMapper.toEntity(any(UserUpdateRequestDTO.class))).thenReturn(testUser);
        when(userServicePort.updateUser(eq(1L), any(User.class)))
                .thenThrow(UserAlreadyExistsException.withEmail("updated@example.com"));

        mockMvc.perform(put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isConflict());

        verify(userMapper).toEntity(any(UserUpdateRequestDTO.class));
        verify(userServicePort).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateUserValidationError() throws Exception {
        UserUpdateRequestDTO invalidRequest = new UserUpdateRequestDTO();
        invalidRequest.setName(""); // Invalid - blank name
        invalidRequest.setEmail("invalid-email"); // Invalid - bad email format

        mockMvc.perform(put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userMapper, never()).toEntity(any(UserUpdateRequestDTO.class));
        verify(userServicePort, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(userServicePort).deleteUser(1L);

        mockMvc.perform(delete("/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userServicePort).deleteUser(1L);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        doThrow(UserNotFoundException.withId(999L)).when(userServicePort).deleteUser(999L);

        mockMvc.perform(delete("/v1/users/999"))
                .andExpect(status().isNotFound());

        verify(userServicePort).deleteUser(999L);
    }

    @Test
    void testCreateUserWithMissingFields() throws Exception {
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(userMapper, never()).toEntity(any(UserCreateRequestDTO.class));
        verify(userServicePort, never()).createUser(any(User.class));
    }

    @Test
    void testCreateUserWithInvalidJson() throws Exception {
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest()); // un JSON inválido es un error del cliente (400), no del servidor

        verify(userMapper, never()).toEntity(any(UserCreateRequestDTO.class));
        verify(userServicePort, never()).createUser(any(User.class));
    }
}