package com.braininventory.leadsphere.lead_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lead_sources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sourceName; // Web, LinkedIn, etc.

    private boolean active = true;
}