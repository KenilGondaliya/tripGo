package com.example.tripGo.security;

import com.example.tripGo.dto.LoginRequestDto;
import com.example.tripGo.dto.LoginResponseDto;
import com.example.tripGo.dto.SignUpRequestDto;
import com.example.tripGo.dto.SignupResponseDto;
import com.example.tripGo.entity.Customer;
import com.example.tripGo.entity.User;
import com.example.tripGo.entity.type.AuthProviderType;
import com.example.tripGo.entity.type.RoleType;
import com.example.tripGo.repository.CustomerRepository;
import com.example.tripGo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(token, user.getId());
    }

    @Transactional
    public User signUpInternal(SignUpRequestDto dto, AuthProviderType provider, String providerId) {
        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);

        if(user != null) throw new IllegalArgumentException("User already exists");

        user = User.builder()
                .username(dto.getUsername())
                .providerId(providerId)
                .providerType(provider)
                .roles(dto.getRoles())
                .build();

        if (provider == AuthProviderType.EMAIL) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user = userRepository.save(user);

        Customer customer = Customer.builder()
                .name(dto.getName())
                .email(dto.getUsername())
                .user(user)
                .phone(dto.getPhone())
                .build();
        customerRepository.save(customer);

        return user;
    }

    // login controller
    public SignupResponseDto signup(SignUpRequestDto dto) {
        User user = signUpInternal(dto, AuthProviderType.EMAIL, null);
        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2Login(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType provider = authUtil.getProviderType(registrationId);
        String providerId = authUtil.determineProviderId(oAuth2User, registrationId);

        User user = userRepository.findByProviderIdAndProviderType(providerId, provider).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User emailUser = email != null ? userRepository.findByUsername(email).orElse(null) : null;

        if (user == null && emailUser == null) {
            String username = email != null ? email : authUtil.determineUsername(oAuth2User, registrationId, providerId);
            SignUpRequestDto signupDto = new SignUpRequestDto(username, null, name,null, Set.of(RoleType.CUSTOMER));
            user = signUpInternal(signupDto, provider, providerId);
        } else if (user != null && email != null && !email.equals(user.getUsername())) {
            user.setUsername(email);
            userRepository.save(user);
        } else if (emailUser != null && user == null) {
            throw new BadCredentialsException("Email already registered with " + emailUser.getProviderType());
        }

//        String token = authUtil.generateAccessToken(user);
//        return ResponseEntity.ok(new LoginResponseDto(token, user.getId()));

        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generateAccessToken(user), user.getId());
        return ResponseEntity.ok(loginResponseDto);
    }
}
