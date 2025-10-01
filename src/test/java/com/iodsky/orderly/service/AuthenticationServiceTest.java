package com.iodsky.orderly.service;

import com.iodsky.orderly.exception.DuplicateResourceException;
import com.iodsky.orderly.model.Role;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.RoleRepository;
import com.iodsky.orderly.repository.UserRepository;
import com.iodsky.orderly.request.LoginRequest;
import com.iodsky.orderly.request.SignupRequest;
import com.iodsky.orderly.response.AuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setup() {
        signupRequest = SignupRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("plainPassword")
                .build();

        loginRequest = LoginRequest.builder()
                .username("johndoe")
                .password("plainPassword")
                .build();

        role = Role.builder()
                .id(UUID.randomUUID())
                .role("CUSTOMER")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("johndoe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(role)
                .build();
    }

    @Nested
    @DisplayName("Register tests")
    class RegisterTests {

        @Test
        void shouldRegisterUserSuccessfully() {
            when(roleRepository.findByRole("CUSTOMER")).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

            AuthenticationResponse response = authenticationService.register(signupRequest);

            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());

            verify(roleRepository).findByRole("CUSTOMER");
            verify(passwordEncoder).encode("plainPassword");
            verify(userRepository).save(any(User.class));
            verify(jwtService).generateToken(any(User.class));
        }

        @Test
        void shouldThrowIfDefaultRoleNotFound() {
            when(roleRepository.findByRole("CUSTOMER")).thenReturn(Optional.empty());

            assertThrows(IllegalStateException.class,
                    () -> authenticationService.register(signupRequest));

            verify(roleRepository).findByRole("CUSTOMER");
            verifyNoInteractions(passwordEncoder, userRepository, jwtService);
        }

        @Test
        void shouldThrowDuplicateResourceExceptionIfUserAlreadyExists() {
            when(roleRepository.findByRole("CUSTOMER")).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThrows(DuplicateResourceException.class,
                    () -> authenticationService.register(signupRequest));

            verify(roleRepository).findByRole("CUSTOMER");
            verify(passwordEncoder).encode("plainPassword");
            verify(userRepository).save(any(User.class));
            verifyNoInteractions(jwtService);
        }
    }

    @Nested
    @DisplayName("Authenticate tests")
    class AuthenticateTests {

        @Test
        void shouldAuthenticateSuccessfully() {
            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
            when(jwtService.generateToken(user)).thenReturn("jwt-token");

            AuthenticationResponse response = authenticationService.authenticate(loginRequest);

            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository).findByUsername("johndoe");
            verify(jwtService).generateToken(user);
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class,
                    () -> authenticationService.authenticate(loginRequest));

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository).findByUsername("johndoe");
            verifyNoInteractions(jwtService);
        }
    }
}
