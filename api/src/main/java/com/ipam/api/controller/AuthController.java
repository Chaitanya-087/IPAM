package com.ipam.api.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipam.api.dto.JwtResponse;
import com.ipam.api.dto.LoginBody;
import com.ipam.api.dto.MessageResponse;
import com.ipam.api.dto.SignupBody;
import com.ipam.api.entity.User;
import com.ipam.api.security.DomainUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    JwtEncoder jwtEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    DomainUserService userService;

    @PostMapping("/token")
    @Operation(summary = "Generate Token", description = "Generate Token for user", responses = {
        @ApiResponse(responseCode = "200", description = "Token generated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid username/password supplied"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<JwtResponse> token(@RequestBody LoginBody loginBody) {
        Instant now = Instant.now();
        long expiry = 3600L;
        var username = loginBody.getUsername();
        var password = loginBody.getPassword();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        JwtResponse response = new JwtResponse(
                userService.findByName(username).get().getId(),
                jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(),
                authentication.getName());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "register's user", description = "")
    public ResponseEntity<MessageResponse> registerUser(@RequestBody SignupBody signUpRequest) {
        if (userService.existsByName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        User user = new User();
        user.setName(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully!"));
    }

}
