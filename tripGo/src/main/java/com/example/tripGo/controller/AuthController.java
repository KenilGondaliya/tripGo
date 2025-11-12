package com.example.tripGo.controller;

import com.example.tripGo.dto.LoginRequestDto;
import com.example.tripGo.dto.LoginResponseDto;
import com.example.tripGo.dto.SignUpRequestDto;
import com.example.tripGo.dto.SignupResponseDto;
import com.example.tripGo.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/oauth2/code/google")
    public String oauth2Success() {
        return "redirect:/?loggedIn=true";
    }
}
