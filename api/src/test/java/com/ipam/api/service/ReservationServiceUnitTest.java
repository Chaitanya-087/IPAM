package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ReservationServiceUnitTest {

  @InjectMocks
  private ReservationService reservationService;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private NetworkObjectRepository networkObjectsRepository;

  private Reservation reservation;
  private NetworkObject networkObject;

  @BeforeAll
  public void setUp() {
    networkObject = new IPAddress();
    networkObject.setId(1L);
    networkObject.setUser(null);
    networkObject.setCreatedAt(LocalDateTime.now());
    networkObject.setUpdatedAt(LocalDateTime.now());
    networkObject.setExpiration(null);
    networkObject.setStatus(Status.AVAILABLE);

    reservation = new Reservation();
    reservation.setId(1l);
    reservation.setPurpose("purpose");
    reservation.setNetworkObject(networkObject);
    reservation.setReleaseDate(LocalDateTime.now().plusHours(1));
  }

  @Test
  void testReserve_ValidReservation() {
    String expectedResult = "network object reserved";

    given(networkObjectsRepository.findById(any(Long.class)))
      .willReturn(Optional.of(networkObject));

    String result = reservationService.reserve(1L, reservation);

    assertEquals(Status.RESERVED, networkObject.getStatus());
    assertEquals(expectedResult, result);
  }

  @Test
  void testReserve_InvalidOperation() {
    // Change the status of the network object to RESERVED (Invalid scenario)
    networkObject.setStatus(Status.RESERVED);

    String expectedResult = "Invalid operation";

    String result = reservationService.reserve(1L, reservation);

    // Verify that the status of the network object is not changed
    assertEquals(Status.RESERVED, networkObject.getStatus());

    // Verify the expected result message
    assertEquals(expectedResult, result);
  }

  @Test
  void testFindAll() {
    // Create some sample reservations for testing
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

    given(reservationRepository.findAll()).willReturn(List.of(reservation,reservation2,reservation3));

    List<ReservationDTO> reservations = reservationService.findAll();

    // Verify that the method returns the list of reservations
    assertEquals(3, reservations.size());
    // assertEquals(reservation.getId(), reservations.get(0));
  }
}
