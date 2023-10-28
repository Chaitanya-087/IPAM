package com.ipam.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipam.api.dto.IPAddressForm;
import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.IPRangeForm;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.dto.SubnetForm;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;
import com.ipam.api.service.IPAddressService;
import com.ipam.api.service.IPRangeService;
import com.ipam.api.service.ReservationService;
import com.ipam.api.service.SubnetService;
import com.ipam.api.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IpamControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private IPAddressService ipAddressService;

  @MockBean
  private SubnetService subnetService;

  @MockBean
  private ReservationService reservationService;

  @MockBean
  private IPRangeService ipRangeService;

  @MockBean
  private UserService userService;

  private IPAddress ipAddress;

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
    ipAddress.setDns(null);
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void shouldReturnAllUsers() throws Exception {
    when(userService.getAllUsers(0,10)).thenReturn(new PageResponse<User>());
    mockMvc
      .perform(get("/api/ipam/users"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void shouldAddIPAddress() throws Exception {
    when(ipAddressService.save(any(IPAddressForm.class))).thenReturn(ipAddress);
    mockMvc
      .perform(
        post("/api/ipam/ipaddresses")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(ipAddress))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.address").exists());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void shouldReturnAllIPAddresses() throws Exception {
    when(ipAddressService.findAll(0,10)).thenReturn(new PageResponse<IPAddress>());
    mockMvc
      .perform(get("/api/ipam/ipaddresses"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  public void testGetIpAddressesWithoutAdminRole() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get("/api/ipam/ipaddresses"))
          .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldReturnAllIPAddressesByUser() throws Exception {

    when(ipAddressService.findByUserId(1l,0,10))
      .thenReturn(new PageResponse<IPAddress>());
    mockMvc
      .perform(get("/api/ipam/users/1/ipaddresses"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldReturnAllAvailableIPAddresses() throws Exception {
    when(ipAddressService.findAllAvailable(0,10))
      .thenReturn(new PageResponse<IPAddress>());
    mockMvc
      .perform(get("/api/ipam/ipaddresses/available"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldAllocateIPAddress() throws Exception {
    when(ipAddressService.allocate(1l, 1l))
      .thenReturn("IP Address allocated successfully");

    mockMvc
      .perform(
        post("/api/ipam/allocate/ipaddresses/1/users/1")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldAssignDns() throws Exception {
    when(ipAddressService.assignDomainName(1l))
      .thenReturn("DNS assigned successfully");

    mockMvc
      .perform(
        post("/api/ipam/ipaddresses/1/dns")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testAdminIpScan() throws Exception {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(1l);
    stat.setInuseCount(1l);
    stat.setReservedCount(1l);

    when(ipAddressService.getStats()).thenReturn(stat);

    mockMvc
      .perform(get("/api/ipam/admin/ip-scan"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.availableCount").value(1l))
      .andExpect(jsonPath("$.inuseCount").value(1l))
      .andExpect(jsonPath("$.reservedCount").value(1l));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testAddIPRange() throws Exception {
    IPRangeForm request = new IPRangeForm();

    when(ipRangeService.save(request)).thenReturn(new IPRangeDTO());

    mockMvc
      .perform(
        post("/api/ipam/ipranges")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testGetAllIPRanges() throws Exception {
    when(ipRangeService.findAll(0,10))
      .thenReturn(new PageResponse<IPRange>());

    mockMvc
      .perform(get("/api/ipam/ipranges"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void testGetAllAvailableIPRanges() throws Exception {
    when(ipRangeService.findAllAvailable(0,10))
      .thenReturn(new PageResponse<IPRange>());

    mockMvc
      .perform(get("/api/ipam/ipranges/available"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void testGetAllIPRangesByUser() throws Exception {
    when(ipRangeService.findByUserId(1l,0,10))
      .thenReturn(new PageResponse<IPRange>());

    mockMvc
      .perform(get("/api/ipam/users/1/ipranges"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldAllocateIPRange() throws Exception {
    when(ipRangeService.allocate(1l, 1l))
      .thenReturn("IP Range allocated successfully");

    mockMvc
      .perform(
        post("/api/ipam/allocate/ipranges/1/users/1")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testAdminIpRangeScan() throws Exception {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(1l);
    stat.setInuseCount(1l);
    stat.setReservedCount(1l);

    when(ipRangeService.getStats()).thenReturn(stat);

    mockMvc
      .perform(get("/api/ipam/admin/iprange-scan"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.availableCount").value(1l))
      .andExpect(jsonPath("$.inuseCount").value(1l))
      .andExpect(jsonPath("$.reservedCount").value(1l));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testAddSubnet() throws Exception {
    SubnetForm request = new SubnetForm();

    when(subnetService.save(request)).thenReturn(new SubnetDTO());

    mockMvc
      .perform(
        post("/api/ipam/subnets")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testGetAllSubnets() throws Exception {
    when(subnetService.findAll(0,10))
      .thenReturn(new PageResponse<Subnet>());

    mockMvc
      .perform(get("/api/ipam/subnets"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void testGetAllAvailableSubnets() throws Exception {
    when(subnetService.findAllAvailable(0,10))
      .thenReturn(new PageResponse<Subnet>());

    mockMvc
      .perform(get("/api/ipam/subnets/available"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void testGetAllSubnetsByUser() throws Exception {
    when(subnetService.findByUserId(1l,0,10))
      .thenReturn(new PageResponse<Subnet>());

    mockMvc
      .perform(get("/api/ipam/users/1/subnets"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldAllocateSubnet() throws Exception {
    when(subnetService.allocate(1l, 1l))
      .thenReturn("Subnet allocated successfully");

    mockMvc
      .perform(
        post("/api/ipam/allocate/subnets/1/users/1")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testAdminSubnetScan() throws Exception {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(1l);
    stat.setInuseCount(1l);
    stat.setReservedCount(1l);

    when(subnetService.getStats()).thenReturn(stat);

    mockMvc
      .perform(get("/api/ipam/admin/subnet-scan"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.availableCount").value(1l))
      .andExpect(jsonPath("$.inuseCount").value(1l))
      .andExpect(jsonPath("$.reservedCount").value(1l));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testReserve() throws Exception {
    long objectId = 1L;
    Reservation reservation = new Reservation();
    String expectedMessage = "Reservation Successful";

    when(reservationService.reserve(objectId, reservation))
      .thenReturn(expectedMessage);

    mockMvc
      .perform(
        post("/api/ipam/reserve/network-object/{id}", objectId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reservation))
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testGetAllReservations() throws Exception {
    when(reservationService.findAll(0,10))
      .thenReturn(new PageResponse<Reservation>());

    mockMvc
      .perform(get("/api/ipam/reservations"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void testGetIpAddressesByIpRangeId() throws Exception {
    when(ipRangeService.findAllIpAddress(1l,0,10))
      .thenReturn(new PageResponse<IPAddress>());

    mockMvc
      .perform(get("/api/ipam/ipranges/{ipRangeId}/ipaddresses", 1l))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void testGetAvailableIpAddressesByIpRangeId() throws Exception {
    when(ipRangeService.findAllAvailableAddressesInRange(1l,0,10))
      .thenReturn(new PageResponse<IPAddress>());

    mockMvc
      .perform(get("/api/ipam/ipranges/{ipRangeId}/ipaddresses/available", 1l))
      .andExpect(status().isOk());
  }
}
