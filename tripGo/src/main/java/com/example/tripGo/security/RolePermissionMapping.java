package com.example.tripGo.security;

import com.example.tripGo.entity.type.Permission;
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
    private static final Map<RoleType, Set<Permission>> map = Map.of(
            RoleType.CUSTOMER, Set.of(Permission.BUS_READ),
            RoleType.ADMIN, Set.of(Permission.BUS_CREATE,
                    Permission.BUS_UPDATE,
                    Permission.BUS_DELETE,
                    Permission.BUS_READ
            )
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(RoleType role) {
        return map.getOrDefault(role, Set.of()).stream()
                .map(p -> new SimpleGrantedAuthority( "ROLE_" + p.name()))
                .collect(Collectors.toSet());
    }


}
