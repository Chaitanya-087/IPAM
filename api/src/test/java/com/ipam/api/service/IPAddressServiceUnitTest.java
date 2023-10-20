package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class IPAddressServiceUnitTest {

    @InjectMocks
    private IPAddressService ipAddressService;

    @Mock
    private IPAddressRepository ipAddressRepository;

    private IPAddress ipAddress;

    @Mock
    private UserRepository userRepository;

    @BeforeAll
    public void setUp() {
        ipAddress = new IPAddress();
        ipAddress.setId(1l);
        ipAddress.setUser(null);
        ipAddress.setStatus(Status.AVAILABLE);
        ipAddress.setCreatedAt(LocalDateTime.now());
        ipAddress.setUpdatedAt(LocalDateTime.now());
        ipAddress.setExpiration(null);
        ipAddress.setAddress("192.78.9.1");
    }

    @Test
    public void givenIpAddress_whenSaved_thenReturnIpAddress() {
        given(ipAddressRepository.save(any(IPAddress.class))).willReturn(ipAddress);

        IPAddress result = ipAddressService.save(ipAddress);

        assertEquals(ipAddress.getAddress(), result.getAddress());
    }

    @Test
    public void givenIpAddresses_whenFindAll_thenReturnIpAddresses() {
        given(ipAddressRepository.findAll()).willReturn(List.of(ipAddress));

        List<IPAddress> ipAddresses = ipAddressService.findAll();

        assertEquals(ipAddresses.size(), 1);
    }

    @Test
    public void givenUserId_whenFindByUserId_thenReturnIpAddresses() {
        Long userId = 123L;

        given(ipAddressRepository.findByUserId(userId)).willReturn(List.of(ipAddress));

        List<IPAddress> ipAddresses = ipAddressService.findByUserId(userId);

        assertEquals(ipAddresses.size(), 1);
        assertEquals(ipAddresses.get(0).getAddress(), ipAddress.getAddress());
    }

    @Test
    public void testAllocateValidIPAddressAndUser() {
        Long ipAddressId = 1L;
        Long userId = 2L;

        IPAddress ipAddress = new IPAddress();
        ipAddress.setStatus(Status.AVAILABLE);

        User user = new User();

        given(ipAddressRepository.findById(ipAddressId)).willReturn(Optional.of(ipAddress));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(ipAddressRepository.save(any(IPAddress.class))).willReturn(ipAddress);

        String result = ipAddressService.allocate(ipAddressId, userId);

        assertEquals("ipaddress allocated with expiration date - " + ipAddress.getExpiration(), result);
        assertEquals(Status.IN_USE, ipAddress.getStatus());
        assertEquals(user, ipAddress.getUser());
    }

    @Test
    public void givenAvailableIPAddresses_whenFindAllAvailable_thenReturnAvailableIPAddresses() {
        ipAddress.setStatus(Status.AVAILABLE);
        given(ipAddressRepository.findByStatus(Status.AVAILABLE)).willReturn(List.of(ipAddress));

        List<IPAddress> result = ipAddressService.findAllAvailable();

        assertEquals(1, result.size());
        assertEquals(ipAddress.getAddress(), result.get(0).getAddress());
    }

    @Test
    public void testAllocateInvalidIPAddress() {
        Long ipAddressId = 1L;
        Long userId = 2L;

        IPAddress ipAddress = new IPAddress();
        ipAddress.setStatus(Status.IN_USE);

        User user = new User();

        given(ipAddressRepository.findById(ipAddressId)).willReturn(Optional.of(ipAddress));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        String result = ipAddressService.allocate(ipAddressId, userId);

        assertEquals("Invalid operation", result);
    }

    @Test
    public void testAllocateInvalidUser() {
        Long ipAddressId = 1L;
        Long userId = 2L;

        given(ipAddressRepository.findById(ipAddressId)).willReturn(Optional.of(new IPAddress()));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        String result = ipAddressService.allocate(ipAddressId, userId);

        assertEquals("Invalid user", result);
    }

}
