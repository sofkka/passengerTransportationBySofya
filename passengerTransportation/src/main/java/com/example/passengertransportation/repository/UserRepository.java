package com.example.passengertransportation.repository;

import com.example.passengertransportation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String userLogin);
    User findByPhoneNumber(String phoneNumber);
}