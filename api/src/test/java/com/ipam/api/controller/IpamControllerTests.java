package com.ipam.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipam.api.dto.UserDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.service.IPAddressService;
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
public class IpamControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private IPAddressService ipAddressService;

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
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  public void shouldSaveIPAddress() throws Exception {
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
  public void shouldReturnAllIPAddresses() throws Exception {
    when(ipAddressService.findAll()).thenReturn(List.of(ipAddress));
    mockMvc
      .perform(get("/api/ipam/ipaddresses"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_ADMIN" })
  public void shouldReturnAllUsers() throws Exception {
    UserDTO user1 = new UserDTO();
    user1.setId(1l);
    user1.setName("test");
    user1.setEmail("test123gmail.com");
    user1.setIpAddressesCount(1l);
    user1.setIpRangesCount(1l);
    user1.setDnsRecordsCount(1l);
    user1.setSubnetsCount(1l);


    UserDTO user2 = new UserDTO();
    user2.setId(2l);
    user2.setName("test2");
    user2.setEmail("test123gmail.com");
    user2.setIpAddressesCount(1l);
    user2.setIpRangesCount(1l);
    user2.setDnsRecordsCount(1l);
    user2.setSubnetsCount(1l);

    
    when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

    mockMvc
      .perform(get("/api/ipam/users"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", Matchers.hasSize(2)));
  }

  @Test
  @WithMockUser(authorities = { "SCOPE_ROLE_USER" })
  public void shouldReturnAllIPAddressesByUser() throws Exception {
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
}
