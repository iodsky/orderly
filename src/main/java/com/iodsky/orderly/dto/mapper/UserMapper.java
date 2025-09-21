package com.iodsky.orderly.dto.mapper;

import com.iodsky.orderly.dto.user.AddUserDto;

import com.iodsky.orderly.dto.user.UserDto;
import com.iodsky.orderly.exceptions.ResourceNotFoundException;
import com.iodsky.orderly.model.Role;
import com.iodsky.orderly.model.User;
import com.iodsky.orderly.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(AddUserDto dto) {
        Role role = roleRepository.findByRole(dto.getRole()).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(role)
                .build();
    }
}
