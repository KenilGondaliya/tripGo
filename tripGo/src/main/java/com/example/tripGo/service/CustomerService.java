package com.example.tripGo.service;

import com.example.tripGo.entity.Customer;
import com.example.tripGo.repository.CustomerRepository;
import com.example.tripGo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;

    public Customer getCurrentCustomer() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return customerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
