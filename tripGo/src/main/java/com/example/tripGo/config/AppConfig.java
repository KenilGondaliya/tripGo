package com.example.tripGo.config;

import com.example.tripGo.dto.RouteRequestDto;
import com.example.tripGo.dto.RouteResponseDto;
import com.example.tripGo.entity.Route;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){

        ModelMapper mapper = new ModelMapper();

        // RouteRequestDto → Route (for creation)
        mapper.addMappings(new PropertyMap<RouteRequestDto, Route>() {
            @Override
            protected void configure() {
                skip(destination.getRouteId()); // Don’t map ID
                skip(destination.getBus());     // Bus set manually
            }
        });

        // Route → RouteResponseDto (for response)
        mapper.addMappings(new PropertyMap<Route, RouteResponseDto>() {
            @Override
            protected void configure() {
                // Example: Map nested fields if needed
                map().setBusId(source.getBus().getBusId());
            }
        });

        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
    UserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("admin")
                .password(passwordEncoder().encode("pass"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.withUsername("costumer")
                .password(passwordEncoder().encode("pass"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }

}
