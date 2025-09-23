package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.auth.AuthenticateResponse;
import com.iodsky.orderly.dto.auth.LoginDto;
import com.iodsky.orderly.dto.auth.SignupDto;
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

    @PostMapping("/register")
    public ResponseEntity<AuthenticateResponse> register(@Valid @RequestBody SignupDto request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@Valid @RequestBody LoginDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
