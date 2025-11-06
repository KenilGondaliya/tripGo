package com.example.tripGo.repository;

import com.example.tripGo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByUser_Id(Long userId);

    Optional<Customer> findByUser_Username(String username);
}