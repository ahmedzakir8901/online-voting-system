package com.voting.repository;

import com.voting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);


    Optional<User> findByCnic(String cnic);


    Optional<User> findByPhoneNumber(String phoneNumber);


    boolean existsByEmail(String email);


    boolean existsByCnic(String cnic);


    boolean existsByPhoneNumber(String phoneNumber);

}