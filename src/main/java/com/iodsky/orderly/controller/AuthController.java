package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.mapper.UserMapper;
import com.iodsky.orderly.dto.user.SignupDto;
import com.iodsky.orderly.dto.user.UserDto;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody SignupDto request) {
        User saved = authenticationService.signup(request);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

}
