package com.example.tripGo.dto;

import com.example.tripGo.entity.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private boolean active;  // Make sure this matches isEnabled() or isActive() in User entity
    private Set<RoleType> roles;  }

