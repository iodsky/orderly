package com.iodsky.orderly.service.auth;

import com.iodsky.orderly.dto.auth.AuthenticateResponse;
import com.iodsky.orderly.dto.auth.LoginDto;
import com.iodsky.orderly.dto.auth.SignupDto;
import com.iodsky.orderly.exceptions.DuplicateResourceException;
import com.iodsky.orderly.model.Role;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.RoleRepository;
import com.iodsky.orderly.repository.UserRepository;
import com.iodsky.orderly.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticateResponse register(SignupDto dto) {
        Role role = roleRepository.findByRole("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Default role CUSTOMER not found"));

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .build();

        try {
            userRepository.save(user);
            String token = jwtService.generateToken(user);
            return AuthenticateResponse.builder().token(token).build();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Username or email already exists");
        }
    }

    public AuthenticateResponse authenticate(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );
        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(
                () -> new BadCredentialsException("User not found.")
        );
        String token = jwtService.generateToken(user);
        return AuthenticateResponse.builder().token(token).build();
    }
}
