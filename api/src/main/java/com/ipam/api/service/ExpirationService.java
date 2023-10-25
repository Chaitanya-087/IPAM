package com.ipam.api.service;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpirationService {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private NetworkObjectRepository networkObjectsRepository;

  @Scheduled(fixedRateString = "${fetch-rate}")
  @Transactional
  public void processExpiredReservations() {
    List<Reservation> reservations = reservationRepository.findAll();
    for (Reservation reservation : reservations) {
      if (reservation.getReleaseDate().isBefore(LocalDateTime.now())) {
        reservation.getNetworkObject().setStatus(Status.AVAILABLE);
        reservationRepository.delete(reservation);
      }
    }
  }

  @Scheduled(fixedRateString = "${fetch-rate}")
  @Transactional
  public void processExpiredNetworkObjects() {
    List<NetworkObject> networkObjects = networkObjectsRepository.findByStatus(
      Status.IN_USE
    );
    for (NetworkObject networkObject : networkObjects) {
      if (networkObject.getExpiration().isBefore(LocalDateTime.now())) {
        networkObject.setStatus(Status.AVAILABLE);
        networkObject.setExpiration(null);
        networkObject.setUser(null);
        if (networkObject instanceof IPAddress ipAddress) {
          ipAddress.setDns(null);
        }
        networkObjectsRepository.save(networkObject);
      }
    }
  }
}