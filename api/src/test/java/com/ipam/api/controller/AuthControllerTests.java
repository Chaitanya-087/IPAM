package com.ipam.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipam.api.dto.LoginBody;
import com.ipam.api.dto.SignupBody;
import com.ipam.api.entity.User;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.security.DomainUserService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.bouncycastle.jcajce.provider.keystore.BC;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AuthControllerTests {

  @Container
  private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
    "mysql:latest"
  );

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DomainUserService userService;


  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private AuthenticationManager authenticationManager;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void registeringUser() throws Exception {
    SignupBody signupBody = SignupBody
      .builder()
      .username("test")
      .password("test123")
      .email("test123@gmail.com")
      .build();

    mockMvc
      .perform(
        post("/api/auth/signup")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(signupBody))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.message").value("User registered successfully!"));

      Optional<User> userOptional = userRepository.findByName("test");

      if (userOptional.isPresent()) {
          // User found, print the password
          System.out.println(userOptional.get().getPassword());
      } else {
          // User not found, handle this case accordingly
          System.out.println("User 'test' not found in the database.");
      }
  }


  @Test
  public void testUserAuthentication() throws Exception {
    LoginBody loginBody = new LoginBody("testUser", "testPassword");

    List<GrantedAuthority> authorities = Collections.singletonList(
      new SimpleGrantedAuthority("ROLE_USER")
    );
    Authentication auth = new UsernamePasswordAuthenticationToken(
      "testUser",
      "testPassword",
      authorities
    );

    when(
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken("testUser", "testPassword")
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
