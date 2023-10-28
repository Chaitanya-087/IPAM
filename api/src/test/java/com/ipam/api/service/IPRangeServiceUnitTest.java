package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.ipam.api.controller.exception.InvalidException;
import com.ipam.api.controller.exception.NotFoundException;
import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.IPRangeForm;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPRangeRepository;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.util.NetworkUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class IPRangeServiceUnitTest {

  @InjectMocks
  private IPRangeService ipRangeService;

  @Mock
  private IPRangeRepository ipRangeRepository;

  @Mock
  private UserRepository userRepository;

  private IPRange ipRange;

  @Mock
  private NetworkUtil networkUtil;

  @BeforeAll
  public void setUp() {
    ipRange = new IPRange();
    ipRange.setId(1L);
    ipRange.setUser(null);
    ipRange.setCreatedAt(LocalDateTime.now());
    ipRange.setUpdatedAt(LocalDateTime.now());
    ipRange.setExpiration(null);
    ipRange.setStatus(Status.AVAILABLE);
    ipRange.setStartAddress("192.168.1.1");
    ipRange.setEndAddress("192.168.1.5");
    ipRange.setIpAddresses(new ArrayList<>());
  }

  @Test
  void testInvalidRangeSave() {
    IPRangeForm inputRange = new IPRangeForm();
    inputRange.setStartAddress("Invalid");
    inputRange.setEndAddress("Invalid");
      
      when(networkUtil.isValidIp(any(String.class))).thenReturn(false);

      assertThrows(InvalidException.class, () -> {
        ipRangeService.save(inputRange);
      });
  }

  @Test
  void testSavingValidRange() {
    IPRangeForm inputRange = new IPRangeForm();
    inputRange.setStartAddress("192.168.1.1");
    inputRange.setEndAddress("192.168.1.5");

    when(networkUtil.ipToLong("192.168.1.1")).thenReturn(3232235777L);
    when(networkUtil.ipToLong("192.168.1.5")).thenReturn(3232235781L);

    List<IPAddress> mockIpAddresses = new ArrayList<>();
    for (long current = 3232235777L; current <= 3232235781L; current++) {
      String ip = networkUtil.longToIp(current);
      IPAddress ipAddress = new IPAddress();
      ipAddress.setAddress(ip);
      mockIpAddresses.add(ipAddress);
    }

    ipRange.setIpAddresses(mockIpAddresses);

    when(ipRangeRepository.save(Mockito.any(IPRange.class)))
      .thenReturn(ipRange);
    when(networkUtil.isValidIp(any(String.class))).thenReturn(true);
    IPRangeDTO result = ipRangeService.save(inputRange);

    assertEquals("192.168.1.1", result.getStartAddress());
    assertEquals("192.168.1.5", result.getEndAddress());
    assertEquals(5, result.getSize().intValue());
  }

  @Test
  void givenIPRanges_whenFindAll_thenReturnIPRangeDTOs() {
    Page<IPRange> ipRangePage = new PageImpl<>(List.of(ipRange));
    when(ipRangeRepository.findAll(any(PageRequest.class))).thenReturn(ipRangePage);

    PageResponse<IPRange> result = ipRangeService.findAll(0,10);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void givenAvailableIPRanges_whenFindAllAvailable_thenReturnAvailableIPRangeDTOs() {
    ipRange.setStatus(Status.AVAILABLE);
    Page<IPRange> ipRangePage = new PageImpl<>(List.of(ipRange));
    when(ipRangeRepository.findByStatus(any(Status.class), any(PageRequest.class)))
      .thenReturn(ipRangePage);

    PageResponse<IPRange> result = ipRangeService.findAllAvailable(0,10);

    assertEquals(1, result.getTotalElements());

  }

   @Test
    public void testFindByUserIdWhenUserNotFound() {
        Long userId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> ipRangeService.findByUserId(userId, 0, 10));
    }

    @Test
    public void testFindByUserIdWhenUserFound() {
        Mockito.when(userRepository.existsById(any(Long.class))).thenReturn(true);

        List<IPRange> ipRangeList = new ArrayList<>();
        Page<IPRange> page = new PageImpl<>(ipRangeList);

        Mockito.when(ipRangeRepository.findByUserId(any(Long.class),any(PageRequest.class))).thenReturn(page);

        PageResponse<IPRange> result = ipRangeService.findByUserId(1l,0,10);

        assertNotNull(result);
    }

  @Test
  void testAllocateValidIPRangeAndUser() {
    long ipRangeId = 1L;
    long userId = 2L;

    IPRange ipRange = new IPRange();
    ipRange.setId(ipRangeId);
    ipRange.setStatus(Status.AVAILABLE);

    User user = new User();
    user.setId(userId);

    IPAddress ipAddress1 = new IPAddress();
    ipAddress1.setStatus(Status.AVAILABLE);

    IPAddress ipAddress2 = new IPAddress();
    ipAddress2.setStatus(Status.AVAILABLE);

    ipRange.getIpAddresses().add(ipAddress1);
    ipRange.getIpAddresses().add(ipAddress2);

    when(ipRangeRepository.findById(ipRangeId))
      .thenReturn(Optional.of(ipRange));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    String result = ipRangeService.allocate(ipRangeId, userId);

    assertEquals("Ip range allocated", result);

    assertEquals(Status.IN_USE, ipAddress1.getStatus());
    assertEquals(Status.IN_USE, ipAddress2.getStatus());
    assertEquals(user, ipAddress1.getUser());
    assertEquals(user, ipAddress2.getUser());
    assertEquals(Status.IN_USE, ipRange.getStatus());
    assertEquals(user, ipRange.getUser());
  }

  @Test
  void testAllocateInvalidIPRange() {
    Long ipRangeId = 1L;
    Long userId = 2L;
    ipRange.setStatus(Status.IN_USE);

    User user = new User();

    when(ipRangeRepository.findById(ipRangeId))
      .thenReturn(Optional.of(ipRange));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    String result = ipRangeService.allocate(ipRangeId, userId);

    assertEquals("Invalid operation", result);
  }

  @Test
  void testAllocateInvalidUser() {
    Long ipRangeId = 1L;
    Long userId = 2L;

    when(ipRangeRepository.findById(ipRangeId))
      .thenReturn(Optional.of(ipRange));
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    String result = ipRangeService.allocate(ipRangeId, userId);

    assertEquals("Invalid user", result);
  }

  @Test
  void givenStats_whenGetStats_thenReturnStats() {
    given(ipRangeRepository.countByStatus(Status.AVAILABLE)).willReturn(1L);
    given(ipRangeRepository.countByStatus(Status.IN_USE)).willReturn(1L);
    given(ipRangeRepository.countByStatus(Status.RESERVED)).willReturn(1L);

    StatDTO stat = ipRangeService.getStats();

    assertEquals(1L, stat.getAvailableCount());
    assertEquals(1L, stat.getInuseCount());
    assertEquals(1L, stat.getReservedCount());
  }
}
