package com.example.tripGo.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

import static com.example.tripGo.entity.type.PermissionType.BOOKING_WRITE;
import static com.example.tripGo.entity.type.RoleType.ADMIN;
import static com.example.tripGo.entity.type.RoleType.CUSTOMER;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final OAuth2SuccessHandler oAuth2Handler;
//    @Autowired
//    @Qualifier("handlerExceptionResolver")
//    private HandlerExceptionResolver resolver;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionConfig  -> sessionConfig .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/auth/**", "/oauth2/**", "/webjars/**", "/images/**", "/css/**").permitAll()
//                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/admin1", "/bus").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth2 error: {}", exception.getMessage());
                            handlerExceptionResolver.resolveException(request, response, null, exception);
                        })
                        .successHandler(oAuth2Handler)
                )
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) ->{
                            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
                }));
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));  // Allow all origins (dev)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
