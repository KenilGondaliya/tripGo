package com.example.tripGo.security;

import com.example.tripGo.dto.LoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();

        ResponseEntity<LoginResponseDto> loginResponse = authService.handleOAuth2Login(oAuth2User,
                registrationId);
        LoginResponseDto body = loginResponse.getBody();

        response.setContentType("text/html;charset=UTF-8");
        String redirectUrl = "/api/v1/";

        // Return HTML page with JS to save JWT and redirect
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Login Success</title>
                <meta http-equiv="refresh" content="0;url=%s">
            </head>
            <body>
              <script>
                localStorage.setItem('jwt', '%s');
                localStorage.setItem('userId', '%s');
                localStorage.setItem('username', '%s');
                window.location.href = '/api/v1/';
              </script>
            </body>
            </html>
            """.formatted(
                redirectUrl,
                body.getJwt(),
                body.getUserId(),
                body.getUsername() != null ? body.getUsername() : oAuth2User.getAttribute("email"),
                redirectUrl
            );

        response.getWriter().write(html);

//        response.setStatus(loginResponse.getStatusCode().value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write(mapper.writeValueAsString(loginResponse.getBody()));
    }
}
