package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ipam.api.controller.exception.AlreadyExistException;
import com.ipam.api.controller.exception.InvalidException;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.dto.SubnetForm;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;
import com.ipam.api.repository.SubnetRepository;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.util.NetworkUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class SubnetServiceUnitTest {

  @InjectMocks
  private SubnetService subnetService;

  @Mock
  private SubnetRepository subnetRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NetworkUtil networkUtil;

  private Subnet subnet;

  @BeforeEach
  public void setUp() {
    subnet = new Subnet();
    subnet.setId(1L);
    subnet.setUser(null);
    subnet.setCreatedAt(LocalDateTime.now());
    subnet.setUpdatedAt(LocalDateTime.now());
    subnet.setExpiration(null);
    subnet.setName("Test Subnet");
    subnet.setCidr("192.168.1.0/24");
    subnet.setMask("255.255.255.0");
    subnet.setGateway("192.168.1.1");
    subnet.setIpAddresses(new ArrayList<>());
    subnet.setStatus(Status.AVAILABLE);
  }

  @Test
  void testSaveExistingSubnet() {
    SubnetForm inputSubnet = new SubnetForm();
    inputSubnet.setName("Test Subnet");
    inputSubnet.setCidr("192.168.1.0/24");
    inputSubnet.setMask("255.255.255.0");
    inputSubnet.setGateway("192.168.1.1");

    when(networkUtil.isValidCidr("192.168.1.0/24")).thenReturn(true);
    when(subnetRepository.existsByCidr(anyString())).thenReturn(true);
    assertThrowsExactly(AlreadyExistException.class, () -> subnetService.save(inputSubnet));
  }

  @Test
  void testSaveInvalidSubnet() {
    SubnetForm inputSubnet = new SubnetForm();
    inputSubnet.setName("Test Subnet");
    inputSubnet.setCidr("invalid_cidr");

    when(networkUtil.isValidCidr("invalid_cidr")).thenReturn(false);

    assertThrowsExactly(InvalidException.class, () -> subnetService.save(inputSubnet));
  }

  @Test
  void givenSubnetRequest_whenSaved_thenReturnSubnetDTO() {
    SubnetForm inputSubnet = new SubnetForm();
    inputSubnet.setName("Test Subnet");
    inputSubnet.setCidr("192.168.1.0/24");
    inputSubnet.setMask("255.255.255.0");
    inputSubnet.setGateway("192.168.1.1");
    when(networkUtil.ipToLong("192.168.1.0")).thenReturn(3232235776L);
    when(subnetRepository.existsByCidr(anyString())).thenReturn(false);
    when(networkUtil.isValidCidr(anyString())).thenReturn(true);
    List<IPAddress> mockIpAddresses = new ArrayList<>();
    int size = (1 << (32 - 24));
    for (int i = 1; i < size - 1; i++) {
      IPAddress ipAddress = new IPAddress();
      when(networkUtil.longToIp(any(Long.class))).thenReturn("mockIPAddress");
      mockIpAddresses.add(ipAddress);
    }
    subnet.setIpAddresses(mockIpAddresses);
    Subnet savedSubnet = new Subnet();
    savedSubnet.setId(1L);

    when(subnetRepository.save(Mockito.any(Subnet.class))).thenReturn(subnet);

    SubnetDTO result = subnetService.save(inputSubnet);

    assertEquals("Test Subnet", result.getName());
    assertEquals("192.168.1.0/24", result.getCidr());

    int expectedIpAddressCount = (1 << (32 - 24)) - 2; // Subtract 2 for network address and broadcast address
    assertEquals(expectedIpAddressCount, result.getSize().intValue());
  }

  @Test
  void givenSubnets_whenFindAll_thenReturnSubnetDTOs() {
    Page<Subnet> page = new PageImpl<>(List.of(subnet));
    given(subnetRepository.findAll(any(PageRequest.class))).willReturn(page);

    PageResponse<Subnet> result = subnetService.findAll(0,10);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void givenUserId_whenFindByUserId_thenReturnSubnetDTOs() {
    Page<Subnet> page = new PageImpl<>(List.of(subnet));
    when(subnetRepository.findByUserId(anyLong(), any(PageRequest.class))).thenReturn(page);
    when(userRepository.existsById(anyLong())).thenReturn(true);
    PageResponse<Subnet> result = subnetService.findByUserId(1l,0,10);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void givenInvalidUserId_whenFindByUserId_thenThrowNotFoundException() {
    when(userRepository.existsById(any(Long.class))).thenReturn(false);

    assertThrowsExactly(InvalidException.class, () -> subnetService.findByUserId(1l,0,10));
  }

  @Test
  void givenAvailableSubnets_whenFindAllAvailable_thenReturnAvailableSubnetDTOs() {
    subnet.setStatus(Status.AVAILABLE);
    Page<Subnet> page = new PageImpl<>(List.of(subnet));
    when(subnetRepository.findByStatus(any(Status.class), any(PageRequest.class)))
      .thenReturn(page);  
    PageResponse<Subnet> result = subnetService.findAllAvailable(0,10);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void testAllocateValidSubnetAndUser() {
    long userId = 1L;
    long subnetId = 2L;

    // Create mock Subnet and User objects
    Subnet subnet = new Subnet();
    subnet.setId(subnetId);
    subnet.setStatus(Status.AVAILABLE);

    User user = new User();
    user.setId(userId);

    // Create mock IPAddress objects with the status AVAILABLE
    IPAddress ipAddress1 = new IPAddress();
    ipAddress1.setStatus(Status.AVAILABLE);

    IPAddress ipAddress2 = new IPAddress();
    ipAddress2.setStatus(Status.AVAILABLE);

    subnet.getIpAddresses().add(ipAddress1);
    subnet.getIpAddresses().add(ipAddress2);

    // Set up the behavior of the subnetOpt and userOpt mocks
    when(subnetRepository.findById(subnetId)).thenReturn(Optional.of(subnet));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Call the allocate method
    String result = subnetService.allocate(subnetId, userId);

    // Verify the result
    assertEquals("Subnet is allocated", result);

    // Verify that IPAddress statuses and Subnet status were updated
    assertEquals(Status.IN_USE, ipAddress1.getStatus());
    assertEquals(Status.IN_USE, ipAddress2.getStatus());
    assertEquals(Status.IN_USE, subnet.getStatus());
    assertEquals(user, subnet.getUser());

    // Verify that subnetRepository.save was called
    verify(subnetRepository, times(1)).save(subnet);
  }

  @Test
  void testAllocateInvalidSubnet() {
    Long subnetId = 1L;
    Long userId = 2L;

    Subnet subnet = new Subnet();
    subnet.setStatus(Status.IN_USE);

    User user = new User();

    when(subnetRepository.findById(subnetId)).thenReturn(Optional.of(subnet));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    String result = subnetService.allocate(subnetId, userId);

    assertEquals("Invalid operation", result);
  }

  @Test
  void testAllocateInvalidUser() {
    Long subnetId = 1L;
    Long userId = 2L;

    Mockito
      .when(subnetRepository.findById(subnetId))
      .thenReturn(Optional.of(subnet));
    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

    String result = subnetService.allocate(subnetId, userId);

    assertEquals("Invalid user", result);
  }

  @Test
  void givenStats_whenGetStats_thenReturnStats() {
    given(subnetRepository.countByStatus(Status.AVAILABLE)).willReturn(1L);
    given(subnetRepository.countByStatus(Status.IN_USE)).willReturn(1L);
    given(subnetRepository.countByStatus(Status.RESERVED)).willReturn(1L);

    StatDTO stat = subnetService.getStats();

    assertEquals(1L, stat.getAvailableCount());
    assertEquals(1L, stat.getInuseCount());
    assertEquals(1L, stat.getReservedCount());
  }
}
