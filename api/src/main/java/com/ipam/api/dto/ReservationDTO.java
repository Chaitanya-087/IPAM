package com.ipam.api.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDTO {

    private Long id;
    private String purpose;
    private LocalDateTime releaseDate;
    private String type;
    private String identifier;
}
