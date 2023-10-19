package com.ipam.api.controller;

import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.MessageResponse;
import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.dto.UserDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Subnet;
import com.ipam.api.service.IPAddressService;
import com.ipam.api.service.IPRangeService;
import com.ipam.api.service.ReservationService;
import com.ipam.api.service.SubnetService;
import com.ipam.api.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ipam")
public class IpamController {

  @Autowired
  private IPAddressService ipAddressService;

  @Autowired
  private IPRangeService ipRangeService;

  @Autowired
  private SubnetService subnetService;

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private UserService userService;

  //get all users (ADMIN)
  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public List<UserDTO> getUsers() {
    return userService.getAllUsers();
  }

  //add ipaddress to pool (ADMIN)
  @PostMapping("/ipaddresses")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public ResponseEntity<IPAddress> addIpAddress(@RequestBody IPAddress body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ipAddressService.save(body));
  }

  //get all ipaddresses (ADMIN)
  @GetMapping("/ipaddresses")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public List<IPAddress> getIpAddresses() {
    return ipAddressService.findAll();
  }

  //get all ipaddresses by user (USER)
  @GetMapping("/users/{userId}/ipaddresses")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public List<IPAddress> getIpAddressesByUser(@PathVariable("userId") Long userId) {
    return ipAddressService.findByUserId(userId);
  }

  //get all available ipaddresses (USER) or (ADMIN)
  @GetMapping("/ipaddresses/available")
  public List<IPAddress> getAllAvailableIpAddresses() {
    return ipAddressService.findAllAvailable();
  }

  @PostMapping("/reserve/network-object/{id}")
  public ResponseEntity<MessageResponse> reserveIPAddress(@PathVariable("id") Long id, @RequestBody Reservation body) {
    return ResponseEntity.ok().body(new MessageResponse(reservationService.reserve(id, body)));
  }

  @PostMapping("/ipranges")
  public IPRangeDTO addIPRange(@RequestBody IPRange body) {
    return ipRangeService.save(body);
  }
  
  @GetMapping("/ipranges")
  public List<IPRangeDTO> getAllIpRangeDTOs() {
    return ipRangeService.findAll();  
  }

  @GetMapping("/ipranges/available")
  public List<IPRangeDTO> getAllAvailableIpRangeDTOs() {
    return ipRangeService.findAllAvailable();
  }

  @GetMapping("/users/{userId}/ipranges")
  public List<IPRangeDTO> getAllIpRangeDTOsByUserId(@PathVariable("userId") Long userId) {
    return ipRangeService.findByUserId(userId);
  }

  @PostMapping("/subnets")
  public SubnetDTO addSubnet(@RequestBody Subnet body) {
    return subnetService.save(body);
  }

  @GetMapping("/subnets")
  public List<SubnetDTO> getAllSubnetDTOs() {
    return subnetService.findAll();
  }

  @GetMapping("/subnets/available")
  public List<SubnetDTO> getAllAvailableSubnetDTOs() {
    return subnetService.findAllAvailable();
  }

  @GetMapping("/users/{userId}/subnets")
  public List<SubnetDTO> getAllSubnetDTOsByUserId(@PathVariable("userId") Long userId) {
    return subnetService.findByUserId(userId);
  }

  @PostMapping("/allocate/ipaddresses/{ipAddressId}/users/{userId}")
  public ResponseEntity<MessageResponse> allocateIpAddress(@PathVariable("ipAddressId") Long ipAddressId, @PathVariable("userId") Long userId) {
    return ResponseEntity.ok().body(new MessageResponse(ipAddressService.allocate(ipAddressId, userId)));
  }

  @PostMapping("/allocate/ipranges/{ipRangeId}/users/{userId}")
  public ResponseEntity<MessageResponse> allocateIpRange(@PathVariable("ipRangeId") Long ipRangeId, @PathVariable("userId") Long userId) {
    return ResponseEntity.ok().body(new MessageResponse(ipRangeService.allocate(ipRangeId, userId)));
  }

  @PostMapping("/allocate/subnets/{subnetId}/users/{userId}")
  public ResponseEntity<MessageResponse> allocateSubnet(@PathVariable("subnetId") Long subnetId, @PathVariable("userId") Long userId) {
    return ResponseEntity.ok().body(new MessageResponse(subnetService.allocate(subnetId, userId)));
  }
}
