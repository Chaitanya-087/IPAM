package com.ipam.api.service;

import com.ipam.api.dto.PageResponse;
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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private NetworkObjectRepository networkObjectsRepository;

  public String reserve(Long id, Reservation body) {
    Optional<NetworkObject> networkObjectOpt = networkObjectsRepository.findById(
      id
    );

    if (networkObjectOpt.isEmpty()) {
      return "Invalid Operation";
    }

    NetworkObject networkObject = networkObjectOpt.get();

    if (networkObject instanceof IPAddress ipAddress) {
      return reserveIPAddress(ipAddress,body);
    } else if (networkObject instanceof IPRange ipRange) {
      return reserveIPRange(ipRange,body);
    } else if (networkObject instanceof Subnet subnet) {
      return reserveSubnet(subnet,body);
    }

    return "Invalid Operation";
  }

  private String reserveIPAddress(IPAddress ipAddress,Reservation body) {
    reserveNetworkObject(ipAddress, LocalDateTime.now().plusHours(1), body.getPurpose());
    return "ip address reservation successful";
  }

  private String reserveIPRange(IPRange ipRange, Reservation body) {
    if (ipRange.getStatus().equals(Status.AVAILABLE)) {
      for (IPAddress ipAddress : ipRange.getIpAddresses()) {
        reserveNetworkObject(ipAddress, LocalDateTime.now().plusHours(1), body.getPurpose());
      }
      reserveNetworkObject(ipRange, LocalDateTime.now().plusHours(1), body.getPurpose());
      return "ip range reservation successful";
    } else {
      return "ip range is not free";
    }
  }

  private String reserveSubnet(Subnet subnet, Reservation body) {
    if (subnet.getStatus().equals(Status.AVAILABLE)) {
      for (IPAddress ipAddress : subnet.getIpAddresses()) {
        reserveNetworkObject(ipAddress, LocalDateTime.now().plusDays(1), body.getPurpose());
      }
      reserveNetworkObject(subnet, LocalDateTime.now().plusDays(1), body.getPurpose());
      return "subnet reservation successful";
    } else {
      return "subnet is not free";
    }
  }

  private void reserveNetworkObject(
    NetworkObject networkObject,
    LocalDateTime releaseTime,
    String purpose
  ) {
    if (networkObject.getStatus().equals(Status.AVAILABLE)) {
      networkObject.setStatus(Status.RESERVED);
      Reservation reservation = new Reservation();
      reservation.setNetworkObject(networkObject);
      reservation.setReleaseDate(releaseTime);
      reservation.setPurpose(purpose);
      reservationRepository.save(reservation);
    }
  }

  public PageResponse<Reservation> findAll(int page, int size) {
    return convertToPageResponse(reservationRepository.findAll(PageRequest.of(page,size)));
  }

  private PageResponse<Reservation> convertToPageResponse(Page<Reservation> page) {
  PageResponse<Reservation> response = new PageResponse<>();
  response.setTotalPages(page.getTotalPages());
  response.setCurrentPage(page.getNumber());
  response.setHasNext(page.hasNext());
  response.setHasPrevious(page.hasPrevious());
  response.setData(page.getContent().stream().map(this::convertToDto).toList());
  response.setTotalElements(page.getTotalElements());
  response.setMaxPageSize(page.getSize());
  response.setCurrentPageSize(page.getNumberOfElements());
  return response;
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