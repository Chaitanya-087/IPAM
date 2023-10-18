package com.ipam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipam.api.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
}
