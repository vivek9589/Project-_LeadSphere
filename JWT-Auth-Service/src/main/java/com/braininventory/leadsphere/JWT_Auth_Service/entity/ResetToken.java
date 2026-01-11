package com.braininventory.leadsphere.JWT_Auth_Service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_tokens")
@Data                   // generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // default constructor
@AllArgsConstructor     // constructor with all fields
@Builder                // builder pattern
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;              // or email if you prefer
    private String token;
    private LocalDateTime expiry;
    private boolean used;
}
