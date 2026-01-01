package com.braininventory.leadsphere.user_service.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Embeddable
@Data
public class Address {

    private String street;


    private String city;

    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
    private String zipCode;



    private String state;


    private String country;
}