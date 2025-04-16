package com.example.passengertransportation.repository;

import com.example.passengertransportation.model.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportTypeRepository extends JpaRepository<TransportType, String> {
}