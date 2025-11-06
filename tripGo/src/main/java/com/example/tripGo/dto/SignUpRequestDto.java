package com.example.tripGo.dto;

import com.example.tripGo.entity.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequestDto {
    private String username;
    private String password;
    private String name;
    private String phone;

    private Set<RoleType> roles = new HashSet<>();
}
