package com.iodsky.orderly.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
}
