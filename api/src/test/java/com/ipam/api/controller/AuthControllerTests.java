package com.ipam.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipam.api.dto.LoginBody;
import com.ipam.api.dto.SignupBody;
import com.ipam.api.entity.User;
import com.ipam.api.security.DomainUserService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DomainUserService userService;

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @MockBean
  private AuthenticationManager authenticationManager;

  @Autowired
  private ObjectMapper objectMapper;

  private User user;

  @BeforeAll
  public void setUp() {
    user = new User();
    user.setId(1l);
    user.setName("test");
    user.setPassword("{bcrypt}" + passwordEncoder.encode("test123"));
    user.setEmail("test123@gmail.com");
  }

  @Test
  void registeringUser() throws JsonProcessingException, Exception {
    SignupBody signupBody = new SignupBody();
    signupBody.setEmail("test123@gmail.com");
    signupBody.setUsername("test");
    signupBody.setPassword("test123");

    mockMvc
      .perform(
        post("/api/auth/signup")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(signupBody))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.message").value("User registered successfully!"));
  }

  @Test
  void registeringUserWithExistingUsername() throws Exception {
    when(userService.existsByName("test")).thenReturn(true);

    SignupBody signupBody = new SignupBody();
    signupBody.setEmail("test123@gmail.com");
    signupBody.setUsername("test");
    signupBody.setPassword("test234");

    mockMvc
      .perform(
        post("/api/auth/signup")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(signupBody))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  void testUserAuthentication() throws Exception {
    LoginBody loginBody = new LoginBody();
    loginBody.setUsername("test");
    loginBody.setPassword("test123");

    when(userService.findByName("test")).thenReturn(Optional.of(user));

    List<GrantedAuthority> authorities = Collections.singletonList(
      new SimpleGrantedAuthority("ROLE_USER")
    );
    Authentication auth = new UsernamePasswordAuthenticationToken(
      "test",
      "test123",
      authorities
    );

    when(
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken("test", "test123")
      )
    )
      .thenReturn(auth);
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/auth/token")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(loginBody))
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.token").isNotEmpty());
  }
}
