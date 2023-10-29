package com.ipam.api.controller;

import com.ipam.api.dto.IPAddressForm;
import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.IPRangeForm;
import com.ipam.api.dto.MessageResponse;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.dto.SubnetForm;
import com.ipam.api.dto.UserDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Reservation;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;
import com.ipam.api.service.IPAddressService;
import com.ipam.api.service.IPRangeService;
import com.ipam.api.service.ReservationService;
import com.ipam.api.service.SubnetService;
import com.ipam.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public PageResponse<User> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return userService.getAllUsers(page, size);
  }

  @PostMapping("/ipaddresses")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public ResponseEntity<IPAddress> addIpAddress(
    @RequestBody IPAddressForm body
  ) {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ipAddressService.save(body));
  }

  @GetMapping("/ipaddresses")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public PageResponse<IPAddress> getIpAddresses(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ipAddressService.findAll(page, size);
  }

  @GetMapping("/users/{userId}/ipaddresses")
  @PreAuthorize(
    "hasAuthority('SCOPE_ROLE_USER')"
  )
  public PageResponse<IPAddress> getIpAddressesByUser(
    @PathVariable("userId") Long userId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ipAddressService.findByUserId(userId, page, size);
  }

  @GetMapping("/ipaddresses/available")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public PageResponse<IPAddress> getAllAvailableIpAddresses(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ipAddressService.findAllAvailable(page, size);
  }

  @PostMapping("/allocate/ipaddresses/{ipAddressId}/users/{userId}")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public ResponseEntity<MessageResponse> allocateIpAddress(
    @PathVariable("ipAddressId") Long ipAddressId,
    @PathVariable("userId") Long userId
  ) {
    return ResponseEntity
      .ok()
      .body(
        new MessageResponse(ipAddressService.allocate(ipAddressId, userId))
      );
  }

  @PostMapping("/ipaddresses/{ipAddressId}/dns")
  public ResponseEntity<MessageResponse> assignDNS(
    @PathVariable("ipAddressId") long ipAddresId
  ) throws IOException {
    return ResponseEntity
      .ok()
      .body(new MessageResponse(ipAddressService.assignDomainName(ipAddresId)));
  }

  @GetMapping("/ipranges/{ipRangeId}/ipaddresses")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public PageResponse<IPAddress> getIpAddressesByIpRangeId(
    @PathVariable("ipRangeId") Long ipRangeId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ipRangeService.findAllIpAddress(ipRangeId, page, size);
  }

  @GetMapping("/ipranges/{ipRangeId}/ipaddresses/available")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public PageResponse<IPAddress> getAvailableIpAddressesByIpRangeId(
    @PathVariable("ipRangeId") Long ipRangeId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ipRangeService.findAllAvailableAddressesInRange(ipRangeId, page, size);
  }

  @GetMapping("/admin/ip-scan")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public StatDTO adminIpScan() {
    return ipAddressService.getStats();
  }

  //ipranges routes
  @PostMapping("/ipranges")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public ResponseEntity<IPRangeDTO> addIPRange(@RequestBody IPRangeForm body) {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(ipRangeService.save(body));
  }

  @GetMapping("/ipranges")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public PageResponse<IPRange> getAllIpRangeDTOs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return ipRangeService.findAll(page, size);
  }

  @GetMapping("/ipranges/available")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public PageResponse<IPRange> getAllAvailableIpRangeDTOs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return ipRangeService.findAllAvailable(page,size);
  }

  @GetMapping("/users/{userId}/ipranges")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public PageResponse<IPRange> getAllIpRangeDTOsByUserId(
    @PathVariable("userId") Long userId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ipRangeService.findByUserId(userId, page, size);
  }

  @PostMapping("/allocate/ipranges/{ipRangeId}/users/{userId}")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public ResponseEntity<MessageResponse> allocateIpRange(
    @PathVariable("ipRangeId") Long ipRangeId,
    @PathVariable("userId") Long userId
  ) {
    return ResponseEntity
      .ok()
      .body(new MessageResponse(ipRangeService.allocate(ipRangeId, userId)));
  }

  @GetMapping("/admin/iprange-scan")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public StatDTO adminIpRangeScan() {
    return ipRangeService.getStats();
  }

  //subnets routes
  @PostMapping("/subnets")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public ResponseEntity<SubnetDTO> addSubnet(@RequestBody SubnetForm body) {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(subnetService.save(body));
  }

  @GetMapping("/subnets")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public PageResponse<Subnet> getAllSubnetDTOs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return subnetService.findAll(page, size);
  }

  @GetMapping("/subnets/available")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public PageResponse<Subnet> getAllAvailableSubnetDTOs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return subnetService.findAllAvailable(page, size);
  }

  @GetMapping("/users/{userId}/subnets")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public PageResponse<Subnet> getAllSubnetDTOsByUserId(@PathVariable("userId") Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return subnetService.findByUserId(userId, page, size);
  }

  @PostMapping("/allocate/subnets/{subnetId}/users/{userId}")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
  public ResponseEntity<MessageResponse> allocateSubnet(
    @PathVariable("subnetId") Long subnetId,
    @PathVariable("userId") Long userId
  ) {
    return ResponseEntity
      .ok()
      .body(new MessageResponse(subnetService.allocate(subnetId, userId)));
  }

  @GetMapping("/admin/subnet-scan")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public StatDTO adminSubnetScan() {
    return subnetService.getStats();
  }

  // reserve for further use
  @PostMapping("/reserve/network-object/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public ResponseEntity<MessageResponse> reserve(
    @PathVariable("id") Long id,
    @RequestBody Reservation body
  ) {
    return ResponseEntity
      .ok()
      .body(new MessageResponse(reservationService.reserve(id, body)));
  }

  @GetMapping("/reservations")
  @Operation(summary = "all reservations", description = "gets all reservations", responses = {
    @ApiResponse(
      responseCode = "200",
      description = "All reservations",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = PageResponse.class))
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Unauthorized",
      content = @Content(
        extensions = {
          @Extension(
            name = "exception",
            properties = {
              @ExtensionProperty(
                name = "exception",
                value = "UnauthorizedException"
              ),
            }
          ),
        }
      )
    ),
    @ApiResponse(responseCode = "403", description = "Forbidden"),
  })
  @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
  public PageResponse<Reservation> adminReservationScan(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return reservationService.findAll(page, size);
  }
}
