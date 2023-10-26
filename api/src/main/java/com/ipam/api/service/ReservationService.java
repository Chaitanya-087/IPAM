package com.ipam.api.service;

import com.ipam.api.dto.ReservationDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private NetworkObjectRepository networkObjectsRepository;

  public String reserve(Long id, Reservation body) {
    Optional<NetworkObject> networkObjectOpt = networkObjectsRepository.findById(id);
    if (networkObjectOpt.isPresent()) {
      if (networkObjectOpt.get() instanceof IPAddress ipAddress) {
        reserveNetworkObj(ipAddress, LocalDateTime.now().plusHours(1));
        return "ip address reservation successful";
      } else if (networkObjectOpt.get() instanceof IPRange ipRange) {
        if (ipRange.getStatus().equals(Status.AVAILABLE)) {
          for (IPAddress ipAddress : ipRange.getIpAddresses()) {
            reserveNetworkObj(ipAddress, LocalDateTime.now().plusHours(1));
          }
          reserveNetworkObj(ipRange, LocalDateTime.now().plusHours(1));
          return "ip range reservation successful";
        } else {
          return "ip range is not free";
        }
      } else if (networkObjectOpt.get() instanceof Subnet subnet) {
        if (subnet.getStatus().equals(Status.AVAILABLE)) {
            for (IPAddress ipAddress : subnet.getIpAddresses()) {
                reserveNetworkObj(ipAddress, LocalDateTime.now().plusDays(1));
            } 
            reserveNetworkObj(subnet, LocalDateTime.now().plusDays(1));
          return "subnet reservation successful";
        } else {
          return "subnet is not free";
        }
      }
    }
    return "Invalid Operation";
  }

  private void reserveNetworkObj(
    NetworkObject networkObject,
    LocalDateTime releaseTime
  ) {
    if (networkObject.getStatus().equals(Status.AVAILABLE)) {
      networkObject.setStatus(Status.RESERVED);
      Reservation reservation = new Reservation();
      reservation.setNetworkObject(networkObject);
      reservation.setReleaseDate(releaseTime);
      reservation.setPurpose("purpose");
      reservationRepository.save(reservation);
    }
  }

  public List<ReservationDTO> findAll() {
    return reservationRepository
      .findAll()
      .stream()
      .map(this::convertToDto)
      .toList();
  }

  private ReservationDTO convertToDto(Reservation reservation) {
    ReservationDTO reservationDTO = new ReservationDTO();
    reservationDTO.setId(reservation.getId());
    reservationDTO.setPurpose(reservation.getPurpose());
    reservationDTO.setReleaseDate(reservation.getReleaseDate());
    if (reservation.getNetworkObject() instanceof IPAddress ipAddress) {
      reservationDTO.setType("IP Address");
      reservationDTO.setIdentifier(ipAddress.getAddress());
    } else if (reservation.getNetworkObject() instanceof IPRange ipRange) {
      reservationDTO.setType("IP Range");
      reservationDTO.setIdentifier(
        ipRange.getStartAddress() + " - " + ipRange.getEndAddress()
      );
    } else if (reservation.getNetworkObject() instanceof Subnet subnet) {
      reservationDTO.setType("Subnet");
      reservationDTO.setIdentifier(subnet.getCidr());
    }
    return reservationDTO;
  }
}
