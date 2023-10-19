package com.ipam.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;

@Service
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NetworkObjectRepository networkObjectsRepository;

    public String reserve(Long id,Reservation body) {
        Reservation reservation = new Reservation();
        Optional<NetworkObject> networkObjectOpt = networkObjectsRepository.findById(id);
        if (networkObjectOpt.isPresent() && networkObjectOpt.get().getStatus().equals(Status.AVAILABLE)) {
            NetworkObject networkObject = networkObjectOpt.get();
            reservation.setNetworkObject(networkObject);
            reservation.setPurpose(body.getPurpose());
            reservationRepository.save(reservation);
            networkObject.setStatus(Status.RESERVED);
            networkObjectsRepository.save(networkObject);
            return "network object reserved";
        }
        return "Invalid operation";
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
