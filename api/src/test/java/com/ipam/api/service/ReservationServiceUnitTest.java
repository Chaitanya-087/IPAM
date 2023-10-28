package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.ipam.api.dto.PageResponse;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

  @InjectMocks
  private ReservationService reservationService;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private NetworkObjectRepository networkObjectsRepository;

  @Test
  void testReserveWithIPAddress() {
    long networkObjectId = 1L;
    Reservation reservation = new Reservation();

    IPAddress ipAddress = new IPAddress();
    ipAddress.setStatus(Status.AVAILABLE);

    when(networkObjectsRepository.findById(networkObjectId))
      .thenReturn(Optional.of(ipAddress));

    String result = reservationService.reserve(networkObjectId, reservation);

    assertEquals("ip address reservation successful", result);
  }

  @Test
  void testReserveWithIPRange() {
    long networkObjectId = 1L;
    Reservation reservation = new Reservation();

    IPRange ipRange = new IPRange();
    ipRange.setStatus(Status.AVAILABLE);

    IPAddress ipAddress1 = new IPAddress();
    ipAddress1.setStatus(Status.AVAILABLE);

    IPAddress ipAddress2 = new IPAddress();
    ipAddress2.setStatus(Status.AVAILABLE);

    ipRange.getIpAddresses().add(ipAddress1);
    ipRange.getIpAddresses().add(ipAddress2);

    ipRange.getIpAddresses().add(ipAddress1);
    ipRange.getIpAddresses().add(ipAddress2);

    when(networkObjectsRepository.findById(networkObjectId))
      .thenReturn(Optional.of(ipRange));

    String result = reservationService.reserve(networkObjectId, reservation);

    assertEquals("ip range reservation successful", result);
  }

  @Test
  void testReserveWithSubnet() {
    long networkObjectId = 1L;
    Reservation reservation = new Reservation();

    Subnet subnet = new Subnet();
    subnet.setStatus(Status.AVAILABLE);

    IPAddress ipAddress1 = new IPAddress();
    ipAddress1.setStatus(Status.AVAILABLE);

    IPAddress ipAddress2 = new IPAddress();
    ipAddress2.setStatus(Status.AVAILABLE);

    subnet.getIpAddresses().add(ipAddress1);
    subnet.getIpAddresses().add(ipAddress2);

    when(networkObjectsRepository.findById(networkObjectId))
      .thenReturn(Optional.of(subnet));

    String result = reservationService.reserve(networkObjectId, reservation);

    assertEquals("subnet reservation successful", result);
  }

  @Test
  void testReserveWithInvalidNetworkObject() {
    long networkObjectId = 1L;
    Reservation reservation = new Reservation();

    when(networkObjectsRepository.findById(networkObjectId))
      .thenReturn(Optional.empty());

    String result = reservationService.reserve(networkObjectId, reservation);

    assertEquals("Invalid Operation", result);
  }

  @Test
  void testReserveWithOccupiedIPRange() {
    long networkObjectId = 1L;
    Reservation reservation = new Reservation();

    IPRange ipRange = new IPRange();
    ipRange.setStatus(Status.IN_USE);

    IPAddress ipAddress1 = new IPAddress();
    ipAddress1.setStatus(Status.IN_USE);

    IPAddress ipAddress2 = new IPAddress();
    ipAddress2.setStatus(Status.IN_USE);

    ipRange.getIpAddresses().add(ipAddress1);
    ipRange.getIpAddresses().add(ipAddress2);

    ipRange.getIpAddresses().add(ipAddress1);
    ipRange.getIpAddresses().add(ipAddress2);

    when(networkObjectsRepository.findById(networkObjectId))
      .thenReturn(Optional.of(ipRange));

    String result = reservationService.reserve(networkObjectId, reservation);

    assertEquals("ip range is not free", result);
  }

  @Test
  void testReserveWithOccupiedSubnet() {
    long networkObjectId = 1L;
    Reservation reservation = new Reservation();

    Subnet subnet = new Subnet();
    subnet.setStatus(Status.IN_USE);

    IPAddress ipAddress1 = new IPAddress();
    ipAddress1.setStatus(Status.IN_USE);

    IPAddress ipAddress2 = new IPAddress();
    ipAddress2.setStatus(Status.IN_USE);

    subnet.getIpAddresses().add(ipAddress1);
    subnet.getIpAddresses().add(ipAddress2);

    when(networkObjectsRepository.findById(networkObjectId))
      .thenReturn(Optional.of(subnet));

    String result = reservationService.reserve(networkObjectId, reservation);

    assertEquals("subnet is not free", result);
  }

  @Test
  void testFindAll() {
    Reservation reservation = new Reservation();
    IPAddress ipAddress = new IPAddress();
    ipAddress.setId(1l);
    ipAddress.setAddress("192.168.73.1");
    ipAddress.setStatus(Status.RESERVED);
    ipAddress.setCreatedAt(LocalDateTime.now());
    ipAddress.setUpdatedAt(LocalDateTime.now());
    ipAddress.setExpiration(null);
    ipAddress.setUser(null);
    reservation.setId(1l);
    reservation.setPurpose("purpose");
    reservation.setReleaseDate(LocalDateTime.now().plusHours(1));
    reservation.setNetworkObject(ipAddress);

    Reservation reservation2 = new Reservation();
    IPRange ipRange = new IPRange();
    ipRange.setId(1l);
    ipRange.setStartAddress(" 192.12.0.1");
    ipRange.setEndAddress("192.12.1.1");
    ipRange.setStatus(Status.RESERVED);
    ipRange.setCreatedAt(LocalDateTime.now());
    ipRange.setUpdatedAt(LocalDateTime.now());
    ipRange.setExpiration(null);
    ipRange.setUser(null);
    reservation2.setId(2l);
    reservation2.setPurpose("purpose");
    reservation2.setReleaseDate(LocalDateTime.now().plusHours(1));
    reservation2.setNetworkObject(ipRange);

    Reservation reservation3 = new Reservation();
    Subnet subnet = new Subnet();
    subnet.setId(1l);
    subnet.setName("subnet");
    subnet.setCidr("192.168.12.0/24");
    subnet.setMask("255.255.255.0");
    subnet.setGateway("192.168.12.1");
    subnet.setStatus(Status.RESERVED);
    subnet.setCreatedAt(LocalDateTime.now());
    subnet.setUpdatedAt(LocalDateTime.now());
    subnet.setExpiration(null);
    subnet.setUser(null);
    reservation3.setId(3l);
    reservation3.setPurpose("purpose");
    reservation3.setReleaseDate(LocalDateTime.now().plusHours(1));
    reservation3.setNetworkObject(subnet);

    Page<Reservation> page = new PageImpl<>(List.of(reservation, reservation2, reservation3));

    given(reservationRepository.findAll(PageRequest.of(0, 10)))
      .willReturn(page);

    PageResponse<Reservation> reservations = reservationService.findAll(0,10);

    assertEquals(3, reservations.getTotalElements());
  }
}
