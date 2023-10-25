package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPRangeRepository;
import com.ipam.api.repository.UserRepository;
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
class IPRangeServiceUnitTest {

  @InjectMocks
  private IPRangeService ipRangeService;

  @Mock
  private IPRangeRepository ipRangeRepository;

  @Mock
  private UserRepository userRepository;

  private IPRange ipRange;

  @BeforeAll
  public void setUp() {
    ipRange = new IPRange();
    ipRange.setId(1L);
    ipRange.setUser(null);
    ipRange.setCreatedAt(LocalDateTime.now());
    ipRange.setUpdatedAt(LocalDateTime.now());
    ipRange.setExpiration(null);
    ipRange.setStatus(Status.AVAILABLE);
    ipRange.setStartAddress("192.168.0.1");
    ipRange.setEndAddress("192.168.0.10");
  }

  @Test
  void givenIPRangeRequest_whenSaved_thenReturnIPRangeDTO() {
    given(ipRangeRepository.save(any(IPRange.class))).willReturn(ipRange);

    IPRangeDTO result = ipRangeService.save(ipRange);

    assertEquals(ipRange.getStartAddress(), result.getStartAddress());
    assertEquals(ipRange.getEndAddress(), result.getEndAddress());
  }

  @Test
  void givenIPRanges_whenFindAll_thenReturnIPRangeDTOs() {
    given(ipRangeRepository.findAll()).willReturn(List.of(ipRange));

    List<IPRangeDTO> result = ipRangeService.findAll();

    assertEquals(1, result.size());
    assertEquals(ipRange.getStartAddress(), result.get(0).getStartAddress());
  }

  @Test
  void givenAvailableIPRanges_whenFindAllAvailable_thenReturnAvailableIPRangeDTOs() {
    ipRange.setStatus(Status.AVAILABLE);
    given(ipRangeRepository.findByStatus(Status.AVAILABLE))
      .willReturn(List.of(ipRange));

    List<IPRangeDTO> result = ipRangeService.findAllAvailable();

    assertEquals(1, result.size());
    assertEquals(ipRange.getStartAddress(), result.get(0).getStartAddress());
  }

  @Test
  void givenUserId_whenFindByUserId_thenReturnIPRangeDTOs() {
    Long userId = 123L;

    given(ipRangeRepository.findByUserId(userId)).willReturn(List.of(ipRange));

    List<IPRangeDTO> result = ipRangeService.findByUserId(userId);

    assertEquals(1, result.size());
    assertEquals(ipRange.getStartAddress(), result.get(0).getStartAddress());
  }

  @Test
  void testAllocateValidIPRangeAndUser() {
    Long ipRangeId = 1L;
    Long userId = 2L;

    IPRange ipRange = new IPRange();
    ipRange.setStatus(Status.AVAILABLE);

    User user = new User();

    when(ipRangeRepository.findById(ipRangeId))
      .thenReturn(Optional.of(ipRange));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(ipRangeRepository.save(any(IPRange.class))).thenReturn(ipRange);

    String result = ipRangeService.allocate(ipRangeId, userId);

    assertEquals(
      "iprange allocated with expiration date - " + ipRange.getExpiration(),
      result
    );
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
