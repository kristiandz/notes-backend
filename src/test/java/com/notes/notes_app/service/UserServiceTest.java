package com.notes.notes_app.service;

import com.notes.notes_app.model.User;
import com.notes.notes_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.RequestRejectedException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        Optional<User> result = Optional.ofNullable(userService.findByUsername("testUser"));
        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknownUser");
        });
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("testUser", result.get().getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_UserDoesNotExist_ReturnsEmptyOptional() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Optional<User> result = userService.findById(userId);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createUser_ValidUser_CreatesUser() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password123");
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        String result = userService.createUser(newUser);
        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).findByUsername(newUser.getUsername());
        verify(passwordEncoder, times(1)).encode(newUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        when(userRepository.findByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        RequestRejectedException exception = assertThrows(
                RequestRejectedException.class,
                () -> userService.createUser(existingUser)
        );
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(existingUser.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }
}

