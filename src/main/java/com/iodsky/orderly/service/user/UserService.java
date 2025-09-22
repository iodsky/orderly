package com.iodsky.orderly.service.user;

import com.iodsky.orderly.exceptions.DuplicateResourceException;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    🔒 Spring Security basics:
    -------------------------
    - When the Spring Security dependency is on the classpath, it automatically secures all endpoints.
    - By default:
        • Every request must be authenticated.
        • A default user is created with:
            username = "user"
            password = printed in the console at startup
        • A login form (HTML) is enabled automatically.

    ⚡ Customizing authentication:
    ------------------------------
    - To authenticate using accounts stored in our own database. We must provide a custom implementation of `UserDetailsService`.
    - `UserDetailsService` is used internally by Spring Security to:
        • Look up a user by username
        • Return user details (username, password, roles/authorities)
    - Here, `CustomUserDetailsService` loads the user from the `UserRepository`.
    - If no user is found, it throws a `UsernameNotFoundException`.
 */

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Username or email already exists");
        }
    }
}
