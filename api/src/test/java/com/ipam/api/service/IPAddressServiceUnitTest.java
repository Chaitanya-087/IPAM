package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ipam.api.controller.exception.AlreadyExistException;
import com.ipam.api.controller.exception.InvalidException;
import com.ipam.api.controller.exception.NotFoundException;
import com.ipam.api.dto.IPAddressForm;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.util.NetworkUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class IPAddressServiceUnitTest {

  @InjectMocks
  private IPAddressService ipAddressService;

  @Mock
  private IPAddressRepository ipAddressRepository;

  private IPAddress ipAddress;

  @Mock
  private Random rand;

  @Mock
  private ResourceLoader resourceLoader;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NetworkUtil networkUtil;

  @BeforeAll
  public void setUp() {
    ipAddress = new IPAddress();
    ipAddress.setId(1l);
    ipAddress.setUser(null);
    ipAddress.setStatus(Status.AVAILABLE);
    ipAddress.setCreatedAt(LocalDateTime.now());
    ipAddress.setUpdatedAt(LocalDateTime.now());
    ipAddress.setExpiration(null);
    ipAddress.setDns(null);
    ipAddress.setAddress("192.168.1.1");
  }

   @Test
    public void testSaveWithValidInput() {
        IPAddressForm body = new IPAddressForm();
        body.setAddress("192.168.1.1");

        when(ipAddressRepository.existsByAddress("192.168.1.1")).thenReturn(false);
        when(networkUtil.isValidIp(any(String.class))).thenReturn(true);
        when(ipAddressRepository.save(any(IPAddress.class))).thenReturn(ipAddress);
        // Call the save method
        IPAddress result = ipAddressService.save(body);

        // Verify that the result is not null and other assertions based on your code
        assertNotNull(result);
        assertEquals("192.168.1.1", result.getAddress());
    }

    @Test
    public void testSaveWithInvalidInput() {
        IPAddressForm body = new IPAddressForm();
        body.setAddress("invalid_address");
        assertThrows(InvalidException.class, () -> ipAddressService.save(body));
    }

    @Test
    public void testSaveWithExistingIP() {
        IPAddressForm body = new IPAddressForm();
        body.setAddress("192.168.1.1");

        when(ipAddressRepository.existsByAddress("192.168.1.1")).thenReturn(true);
        when(networkUtil.isValidIp(any(String.class))).thenReturn(true);
        // Call the save method, which should throw an exception
        assertThrows(AlreadyExistException.class, () -> ipAddressService.save(body));
    }

  @Test
  void givenIpAddresses_whenFindAll_thenReturnIpAddresses() {

    Page<IPAddress> page = new PageImpl<>(List.of(ipAddress));
    given(ipAddressRepository.findAll(any(PageRequest.class))).willReturn(page);

    PageResponse<IPAddress> ipAddresses = ipAddressService.findAll(0,10);

    assertEquals(1, ipAddresses.getTotalElements());
  }

  @Test
  void testFindByValidUserId() {
    Page<IPAddress> page = new PageImpl<>(List.of(ipAddress));
    when(ipAddressRepository.findByUserId(anyLong(), any(PageRequest.class)))
      .thenReturn(page);
    when(userRepository.existsById(anyLong())).thenReturn(true);
    PageResponse<IPAddress> ipAddresses = ipAddressService.findByUserId(1l,0,10);

    assertEquals(1, ipAddresses.getTotalElements());
  }

  @Test
  void testFindByInvalidUserId() {
    when(userRepository.existsById(any(Long.class))).thenReturn(false);

    assertThrows(NotFoundException.class, () -> ipAddressService.findByUserId(1l,0,10));

  }

  @Test
  void testAllocateValidIPAddressAndUser() {
    Long ipAddressId = 1L;
    Long userId = 2L;

    IPAddress ipAddress = new IPAddress();
    ipAddress.setStatus(Status.AVAILABLE);

    User user = new User();

    given(ipAddressRepository.findById(ipAddressId))
      .willReturn(Optional.of(ipAddress));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(ipAddressRepository.save(any(IPAddress.class))).willReturn(ipAddress);

    String result = ipAddressService.allocate(ipAddressId, userId);

    assertEquals(
      "ipaddress allocated with expiration date - " + ipAddress.getExpiration(),
      result
    );
    assertEquals(Status.IN_USE, ipAddress.getStatus());
    assertEquals(user, ipAddress.getUser());
  }

  @Test
  void givenAvailableIPAddresses_whenFindAllAvailable_thenReturnAvailableIPAddresses() {
    ipAddress.setStatus(Status.AVAILABLE);
    Page<IPAddress> page = new PageImpl<>(List.of(ipAddress));
    when(ipAddressRepository.findByStatus(any(Status.class), any(PageRequest.class)))
      .thenReturn(page);
    PageResponse<IPAddress> ipaddresses = ipAddressService.findAllAvailable(0,10);
    assertEquals(1, ipaddresses.getTotalElements());
  }

  @Test
  void testAllocateInvalidIPAddress() {
    Long ipAddressId = 1L;
    Long userId = 2L;

    IPAddress ipAddress = new IPAddress();
    ipAddress.setStatus(Status.IN_USE);

    User user = new User();

    given(ipAddressRepository.findById(ipAddressId))
      .willReturn(Optional.of(ipAddress));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    String result = ipAddressService.allocate(ipAddressId, userId);

    assertEquals("Invalid operation", result);
  }

  @Test
  void testAllocateInvalidUser() {
    Long ipAddressId = 1L;
    Long userId = 2L;

    given(ipAddressRepository.findById(ipAddressId))
      .willReturn(Optional.of(new IPAddress()));
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    String result = ipAddressService.allocate(ipAddressId, userId);

    assertEquals("Invalid user", result);
  }

  @Test
  void givenValidIPAddressId_whenAssignDomainName_thenDomainNameAssigned()
    throws IOException {
    String testWord = "test";
    ipAddress.setStatus(Status.IN_USE);
    InputStream inputStream = new ByteArrayInputStream(testWord.getBytes());
    Resource resource = mock(Resource.class);

    when(ipAddressRepository.findById(any(Long.class)))
      .thenReturn(Optional.of(ipAddress));
    when(ipAddressRepository.save(any(IPAddress.class))).thenReturn(ipAddress);
    when(resource.getInputStream()).thenReturn(inputStream);
    when(resourceLoader.getResource(any(String.class))).thenReturn(resource);
    String result = ipAddressService.assignDomainName(1L);

    assertEquals("Domain name assigned", result);
    assertTrue(ipAddress.getDns().contains(".pro"));
  }

  @Test
  void givenInvalidIPAddressId_whenAssignDomainName_thenInvalidOperation()
    throws IOException {
    long ipAddressId = 1L;

    when(ipAddressRepository.findById(anyLong())).thenReturn(Optional.empty());

    String result = ipAddressService.assignDomainName(ipAddressId);

    assertEquals("Invalid operation", result);
  }

  @Test
  void givenStats_whenGetStats_thenReturnStats() {
    given(ipAddressRepository.countByStatus(Status.AVAILABLE)).willReturn(1L);
    given(ipAddressRepository.countByStatus(Status.IN_USE)).willReturn(1L);
    given(ipAddressRepository.countByStatus(Status.RESERVED)).willReturn(1L);

    StatDTO stat = ipAddressService.getStats();

    assertEquals(1L, stat.getAvailableCount());
    assertEquals(1L, stat.getInuseCount());
    assertEquals(1L, stat.getReservedCount());
  }
}
