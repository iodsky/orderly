package com.iodsky.orderly.service;

import com.iodsky.orderly.exception.DuplicateResourceException;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("plainPassword")
                .build();
    }

    @Nested
    @DisplayName("loadUserByUsername tests")
    class LoadUserByUsernameTests {

        @Test
        void shouldReturnUserIfFound() {
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

            UserDetails result = userService.loadUserByUsername("testUser");

            assertNotNull(result);
            assertEquals("testUser", result.getUsername());
            verify(userRepository).findByUsername("testUser");
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> userService.loadUserByUsername("testUser"));

            verify(userRepository).findByUsername("testUser");
        }
    }

    @Nested
    @DisplayName("getAllUsers tests")
    class GetAllUsersTests {
        @Test
        void shouldReturnAllUsers() {
            when(userRepository.findAll()).thenReturn(List.of(user));

            List<User> result = userService.getAllUsers();

            assertEquals(1, result.size());
            assertEquals(user, result.get(0));
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("addUser tests")
    class AddUserTests {
        @Test
        void shouldEncodePasswordAndSaveUser() {
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User savedUser = userService.addUser(user);

            assertNotNull(savedUser);
            assertEquals("encodedPassword", savedUser.getPassword());

            verify(passwordEncoder).encode("plainPassword");
            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowIfDuplicate() {
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThrows(DuplicateResourceException.class,
                    () -> userService.addUser(user));

            verify(passwordEncoder).encode("plainPassword");
            verify(userRepository).save(user);
        }
    }
}
