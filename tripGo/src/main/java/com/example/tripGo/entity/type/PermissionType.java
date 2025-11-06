package com.example.tripGo.entity.type;

public enum PermissionType {
    BOOKING_READ("booking:read"),
    BOOKING_WRITE("booking:write"),
    BOOKING_DELETE("booking:delete"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    ADMIN_DELETE("admin:delete");

    private final String permission;

    PermissionType(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
