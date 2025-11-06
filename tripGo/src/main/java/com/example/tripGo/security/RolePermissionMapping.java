package com.example.tripGo.security;

import com.example.tripGo.entity.type.PermissionType;
import com.example.tripGo.entity.type.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.tripGo.entity.type.PermissionType.*;
import static com.example.tripGo.entity.type.RoleType.ADMIN;
import static com.example.tripGo.entity.type.RoleType.CUSTOMER;

public class RolePermissionMapping {
    private static final Map<RoleType, Set<PermissionType>> map = Map.of(
            CUSTOMER, Set.of(BOOKING_READ, BOOKING_WRITE),
            ADMIN, Set.of(ADMIN_READ, ADMIN_WRITE, ADMIN_DELETE, BOOKING_READ, BOOKING_WRITE, BOOKING_DELETE)
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(RoleType role) {
        return map.getOrDefault(role, Set.of()).stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
    }
}
