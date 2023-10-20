package com.ipam.api.service;

import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;
import com.ipam.api.repository.SubnetRepository;
import com.ipam.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubnetServiceUnitTest {

    @InjectMocks
    private SubnetService subnetService;

    @Mock
    private SubnetRepository subnetRepository;

    @Mock
    private UserRepository userRepository;

    private Subnet subnet;

    @BeforeEach
    public void setUp() {
        subnet = new Subnet();
        subnet.setId(1L);
        subnet.setUser(null);
        subnet.setCreatedAt(LocalDateTime.now());
        subnet.setUpdatedAt(LocalDateTime.now());
        subnet.setExpiration(null);
        subnet.setName("TestSubnet");
        subnet.setCidr("192.168.0.0/24");
        subnet.setMask("255.255.255.0");
        subnet.setGateway("192.168.0.1");
        subnet.setStatus(Status.AVAILABLE); 
    }

    @Test
    void givenSubnetRequest_whenSaved_thenReturnSubnetDTO() {
        given(subnetRepository.save(any(Subnet.class))).willReturn(subnet);

        SubnetDTO result = subnetService.save(subnet);

        assertEquals(subnet.getName(), result.getName());
        assertEquals(subnet.getCidr(), result.getCidr());
        assertEquals(subnet.getMask(), result.getMask());
        assertEquals(subnet.getGateway(), result.getGateway());
    }

    @Test
    void givenSubnets_whenFindAll_thenReturnSubnetDTOs() {
        given(subnetRepository.findAll()).willReturn(List.of(subnet));

        List<SubnetDTO> result = subnetService.findAll();

        assertEquals(1, result.size());
        assertEquals(subnet.getName(), result.get(0).getName());
    }

    @Test
    void givenUserId_whenFindByUserId_thenReturnSubnetDTOs() {
        Long userId = 123L;

        given(subnetRepository.findByUserId(userId)).willReturn(List.of(subnet));

        List<SubnetDTO> result = subnetService.findByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(subnet.getName(), result.get(0).getName());
    }

    @Test
    void givenAvailableSubnets_whenFindAllAvailable_thenReturnAvailableSubnetDTOs() {
        subnet.setStatus(Status.AVAILABLE);
        given(subnetRepository.findByStatus(Status.AVAILABLE)).willReturn(List.of(subnet));

        List<SubnetDTO> result = subnetService.findAllAvailable();

        assertEquals(1, result.size());
        assertEquals(subnet.getName(), result.get(0).getName());
    }

    @Test
    void testAllocateValidSubnetAndUser() {
        Long subnetId = 1L;
        Long userId = 2L;

        User user = new User();

        when(subnetRepository.findById(subnetId)).thenReturn(Optional.of(subnet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subnetRepository.save(any(Subnet.class))).thenReturn(subnet);

        String result = subnetService.allocate(subnetId, userId);

        assertEquals("subnet allocated with expiration date - " + subnet.getExpiration(), result);
        assertEquals(Status.IN_USE, subnet.getStatus());
        assertEquals(user, subnet.getUser());
    }

    @Test
    void testAllocateInvalidSubnet() {
        Long subnetId = 1L;
        Long userId = 2L;

        Subnet subnet =  new Subnet();
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

        Mockito.when(subnetRepository.findById(subnetId)).thenReturn(Optional.of(subnet));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = subnetService.allocate(subnetId, userId);

        assertEquals("Invalid user", result);
    }
}
