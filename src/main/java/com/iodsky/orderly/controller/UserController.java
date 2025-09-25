package com.iodsky.orderly.controller;

import com.iodsky.orderly.dto.mapper.UserMapper;
import com.iodsky.orderly.request.AddUserRequest;
import com.iodsky.orderly.dto.UserDto;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Creates a new user. Only Admins can perform this action."
    )
    @PostMapping()
    public ResponseEntity<UserDto> createUser(@Valid  @RequestBody AddUserRequest dto) {
        User entity = userMapper.toEntity(dto);
        User user = userService.addUser(entity);
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieves all users. Only Admins can perform this action."
    )
    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers().stream().map(userMapper::toDto).toList();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}

