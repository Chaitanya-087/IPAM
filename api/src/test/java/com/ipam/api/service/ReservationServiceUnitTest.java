package com.ipam.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.repository.NetworkObjectRepository;
import com.ipam.api.repository.ReservationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ReservationServiceUnitTest {

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
        reservation = new Reservation();
        reservation.setId(1l);
        reservation.setPurpose("purpose");

        networkObject = new IPAddress();
        networkObject.setId(1L);
        networkObject.setUser(null);
        networkObject.setCreatedAt(LocalDateTime.now());
        networkObject.setUpdatedAt(LocalDateTime.now());
        networkObject.setExpiration(null);
        networkObject.setStatus(Status.AVAILABLE);
    }

    @Test
    public void testReserve_ValidReservation() {
        String expectedResult = "network object reserved";

        given(networkObjectsRepository.findById(any(Long.class))).willReturn(Optional.of(networkObject));

        String result = reservationService.reserve(1L, reservation);

        assertEquals(Status.RESERVED, networkObject.getStatus());
        assertEquals(expectedResult, result);
    }

    @Test
    public void testReserve_InvalidOperation() {
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
    public void testFindAll() {
        // Create some sample reservations for testing
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        given(reservationRepository.findAll()).willReturn(List.of(reservation1, reservation2));

        List<Reservation> reservations = reservationService.findAll();

        // Verify that the method returns the list of reservations
        assertEquals(2, reservations.size());
        assertEquals(reservation1, reservations.get(0));
        assertEquals(reservation2, reservations.get(1));
    }
}
