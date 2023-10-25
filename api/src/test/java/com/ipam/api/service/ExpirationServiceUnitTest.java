package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExpirationServiceUnitTest {

  @InjectMocks
  private ExpirationService expirationService;

  @Mock
  private ReservationRepository reservationRepository;

  @Mock
  private NetworkObjectRepository networkObjectsRepository;

  @Test
  void testProcessExpiredReservations() {
    Reservation expiredReservation = new Reservation();
    expiredReservation.setReleaseDate(LocalDateTime.now().minusMinutes(1)); // An expired reservation

    List<Reservation> reservations = new ArrayList<>();
    reservations.add(expiredReservation);

    when(reservationRepository.findAll()).thenReturn(reservations);
    NetworkObject ipAddress = new IPAddress();
    ipAddress.setStatus(Status.RESERVED);
    expiredReservation.setNetworkObject(ipAddress);

    expirationService.processExpiredReservations();

    assertEquals(Status.AVAILABLE, ipAddress.getStatus());
    verify(reservationRepository, times(1)).delete(expiredReservation);
  }

  @Test
  void testProcessExpiredNetworkObjects() {
    NetworkObject networkObject = new IPAddress();
    networkObject.setId(1L);
    networkObject.setStatus(Status.IN_USE);
    networkObject.setExpiration(LocalDateTime.now().minusMinutes(1));
    networkObject.setUser(new User());

    List<NetworkObject> networkObjects = new ArrayList<>();
    networkObjects.add(networkObject);

    when(networkObjectsRepository.findByStatus(Status.IN_USE))
      .thenReturn(networkObjects);

    expirationService.processExpiredNetworkObjects();

    assertEquals(Status.AVAILABLE, networkObject.getStatus());
    assertEquals(null, networkObject.getUser());
    assertEquals(null, networkObject.getExpiration());
    assertEquals(null, ((IPAddress) networkObject).getDns());

    verify(networkObjectsRepository, times(1)).save(networkObject);
  }
}
