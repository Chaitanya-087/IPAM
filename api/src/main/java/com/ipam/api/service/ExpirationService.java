package com.ipam.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;

import jakarta.transaction.Transactional;

@Service
public class ExpirationService {
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NetworkObjectRepository networkObjectsRepository;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void scheduleTaskWithFixedRateForReservation() {
        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation : reservations) {
            if (reservation.getReleaseDate().isBefore(LocalDateTime.now())) {
                reservation.getNetworkObject().setStatus(Status.AVAILABLE);
                reservationRepository.delete(reservation);
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void scheduleTaskWithFixedRateForExpiration() {
        List<NetworkObject> networkObjects = networkObjectsRepository.findByStatus(Status.IN_USE);
        for (NetworkObject networkObject : networkObjects) {
            if (networkObject.getExpiration().isBefore(LocalDateTime.now())) {
                networkObject.setStatus(Status.AVAILABLE);
                networkObject.setUser(null);
                networkObjectsRepository.save(networkObject);
            }
        }
    }
}
