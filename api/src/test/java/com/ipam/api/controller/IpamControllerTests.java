package com.ipam.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.ReservationDTO;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.dto.UserDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.service.IPAddressService;
import com.ipam.api.service.IPRangeService;
import com.ipam.api.service.ReservationService;
import com.ipam.api.service.SubnetService;
import com.ipam.api.service.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
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
    UserDTO user1 = new UserDTO();
    user1.setId(1l);
    user1.setName("test");
    user1.setEmail("test123gmail.com");
    user1.setIpAddressesCount(1l);
    user1.setIpRangesCount(1l);
    user1.setSubnetsCount(1l);

    UserDTO user2 = new UserDTO();
    user2.setId(2l);
    user2.setName("test2");
    user2.setEmail("test123gmail.com");
    user2.setIpAddressesCount(1l);
    user2.setIpRangesCount(1l);
    user2.setSubnetsCount(1l);

    when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

    mockMvc
      .perform(get("/api/ipam/users"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", Matchers.hasSize(2)));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  void shouldAddIPAddress() throws Exception {
    when(ipAddressService.save(any(IPAddress.class))).thenReturn(ipAddress);
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
    when(ipAddressService.findAll()).thenReturn(List.of(ipAddress));
    mockMvc
      .perform(get("/api/ipam/ipaddresses"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldReturnAllIPAddressesByUser() throws Exception {
    IPAddress ipAddress = new IPAddress();
    ipAddress.setAddress("192.168.81.0");
    when(ipAddressService.findByUserId(1l))
      .thenReturn(Arrays.asList(ipAddress));
    mockMvc
      .perform(get("/api/ipam/users/1/ipaddresses"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  void shouldReturnAllAvailableIPAddresses() throws Exception {
    when(ipAddressService.findAllAvailable())
      .thenReturn(Arrays.asList(ipAddress));

    mockMvc
      .perform(get("/api/ipam/ipaddresses/available"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
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
  public void testAdminIpScan() throws Exception {
    // Mock the behavior of the ipAddressService.getStats method
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
  public void testAddIPRange() throws Exception {
    IPRange request = new IPRange(); // Create a sample IPRange object

    // Mock the behavior of the ipRangeService.save method
    when(ipRangeService.save(request))
      .thenReturn(new IPRangeDTO(/* specify values */));

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
  public void testGetAllIPRanges() throws Exception {
    when(ipRangeService.findAll())
      .thenReturn(List.of(new IPRangeDTO(), new IPRangeDTO()));

    mockMvc
      .perform(get("/api/ipam/ipranges"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  public void testGetAllAvailableIPRanges() throws Exception {
    when(ipRangeService.findAllAvailable())
      .thenReturn(List.of(new IPRangeDTO(), new IPRangeDTO()));

    mockMvc
      .perform(get("/api/ipam/ipranges/available"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  public void testGetAllIPRangesByUser() throws Exception {
    when(ipRangeService.findByUserId(1l))
      .thenReturn(List.of(new IPRangeDTO(), new IPRangeDTO()));

    mockMvc
      .perform(get("/api/ipam/users/1/ipranges"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
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
    // Mock the behavior of the ipRangeService.getStats method
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
  public void testAddSubnet() throws Exception {
    Subnet request = new Subnet(); 

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
  public void testGetAllSubnets() throws Exception {
    when(subnetService.findAll())
      .thenReturn(List.of(new SubnetDTO(), new SubnetDTO()));

    mockMvc
      .perform(get("/api/ipam/subnets"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  public void testGetAllAvailableSubnets() throws Exception {
    when(subnetService.findAllAvailable())
      .thenReturn(List.of(new SubnetDTO(), new SubnetDTO()));

    mockMvc
      .perform(get("/api/ipam/subnets/available"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  public void testGetAllSubnetsByUser() throws Exception {
    when(subnetService.findByUserId(1l))
      .thenReturn(List.of(new SubnetDTO(), new SubnetDTO()));

    mockMvc
      .perform(get("/api/ipam/users/1/subnets"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
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
  public void testAdminSubnetScan() throws Exception {
    // Mock the behavior of the subnetService.getStats method
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
  public void testReserve() throws Exception {
    long objectId = 1L;
        Reservation reservation = new Reservation(); 
        String expectedMessage = "Reservation Successful"; 

        when(reservationService.reserve(objectId, reservation)).thenReturn(expectedMessage);

        mockMvc.perform(post("/api/ipam/reserve/network-object/{id}", objectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  public void testGetAllReservations() throws Exception {
    when(reservationService.findAll())
      .thenReturn(List.of(new ReservationDTO(), new ReservationDTO()));

    mockMvc
      .perform(get("/api/ipam/reservations"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray());
  }
}
