package com.example.tripGo.controller;

import com.example.tripGo.dto.*;
import com.example.tripGo.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignUpRequestDto dto) {
        return ResponseEntity.ok(authService.signup(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.getCurrentUser(authHeader));
    }

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.isAdmin(authHeader));
    }

    @GetMapping("/oauth2/code/google")
    public String oauth2Success() {
        return "redirect:/?loggedIn=true";
    }

    @GetMapping("/debug")
    public Map<String, Object> debug() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> map = new HashMap<>();
        map.put("principal", a.getPrincipal());
        map.put("authorities", a.getAuthorities());
        map.put("authName", a.getName());
        map.put("isAuthenticated", a.isAuthenticated());
        return map;
    }
}
