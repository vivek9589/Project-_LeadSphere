package com.braininventory.leadsphere.lead_service.entity;


import jakarta.persistence.*;
import lombok.Data;

// SalesTarget.java
@Entity
@Data
@Table(name = "sales_targets")
public class SalesTarget {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Double monthlyTarget;
    private Double quarterlyTarget;
    private Integer targetMonth; // 1-12
    private Integer targetYear;  // 2026
}

