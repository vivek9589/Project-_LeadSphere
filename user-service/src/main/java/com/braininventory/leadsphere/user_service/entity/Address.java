package com.braininventory.leadsphere.user_service.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Embeddable
@Data
public class Address {
    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    private String zipCode;


    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;
}