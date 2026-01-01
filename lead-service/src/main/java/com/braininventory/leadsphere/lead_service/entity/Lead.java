package com.braininventory.leadsphere.lead_service.entity;


import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;



@Entity
@Table(name = "leads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    @Column(nullable = false)
    private String company;

    @NotBlank(message = "Contact name is required")
    @Column(nullable = false)
    private String contactName;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Contact email is required")
    @Column(nullable = false)
    private String contactEmail;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10-15 digits")
    private String contactPhone;

    @NotBlank(message = "Opportunity name is required")
    private String opportunityName;

    @PositiveOrZero(message = "Lead value cannot be negative")
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status = LeadStatus.NEW; // Default value

    private String source;

    //@NotBlank(message = "Lead owner is required")
    private String owner; // Linked to assigned person

    @Column(name = "owner_id")
    private Long ownerId;

   @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime  createdAt;

   @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime  updatedAt;
}