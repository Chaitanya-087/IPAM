package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ipam.api.dto.PageResponse;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.IPRangeRepository;
import com.ipam.api.repository.SubnetRepository;
import com.ipam.api.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
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
public class UserServiceUnitTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private IPRangeRepository ipRangeRepository;

  @Mock
  private IPAddressRepository ipAddressRepository;

  @Mock
  private SubnetRepository subnetRepository;

  @Test
  public void testGetAllUsers() {
    // Create mock data
    List<User> userList = new ArrayList<>();
    User user1 = new User();
    user1.setId(1L);
    user1.setName("User1");
    user1.setEmail("user1@example.com");
    user1.setRole("USER");
    userList.add(user1);
    User user2 = new User();
    user2.setId(2L);
    user2.setName("User2");
    user2.setEmail("user2@example.com");
    user2.setRole("USER");
    userList.add(user2);
    Page<User> page = new PageImpl<>(userList);

    // Mock the behavior of userRepository
    when(userRepository.findByRole("USER", PageRequest.of(0, 10)))
      .thenReturn(page);

    // Mock the behavior of other repositories
    when(ipRangeRepository.countByUser(Mockito.any(User.class))).thenReturn(0L);
    when(ipAddressRepository.countByUser(Mockito.any(User.class)))
      .thenReturn(0L);
    when(subnetRepository.countByUser(Mockito.any(User.class))).thenReturn(0L);

    // Call the getAllUsers method
    PageResponse<User> result = userService.getAllUsers(0, 10);

    // Verify the result
    assertEquals(2, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    assertEquals(2, result.getData().size());
    // Add more assertions based on your specific implementation
  }
}
