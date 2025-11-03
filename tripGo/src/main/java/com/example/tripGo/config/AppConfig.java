package com.example.tripGo.config;

import com.example.tripGo.dto.RouteRequestDto;
import com.example.tripGo.dto.RouteResponseDto;
import com.example.tripGo.entity.Route;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){

        ModelMapper mapper = new ModelMapper();

        // ✅ RouteRequestDto → Route (for creation)
        mapper.addMappings(new PropertyMap<RouteRequestDto, Route>() {
            @Override
            protected void configure() {
                skip(destination.getRouteId()); // Don’t map ID
                skip(destination.getBus());     // Bus set manually
            }
        });

        // ✅ Route → RouteResponseDto (for response)
        mapper.addMappings(new PropertyMap<Route, RouteResponseDto>() {
            @Override
            protected void configure() {
                // Example: Map nested fields if needed
                map().setBusId(source.getBus().getBusId());
            }
        });

        return mapper;
    }



}
