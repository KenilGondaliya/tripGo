package com.example.tripGo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.example.tripGo")
public class TripGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripGoApplication.class, args);
	}

}
