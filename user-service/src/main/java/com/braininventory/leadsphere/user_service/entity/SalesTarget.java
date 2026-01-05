package com.braininventory.leadsphere.user_service.entity;

import jakarta.persistence.*;

@Entity
public class SalesTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double monthlyTarget; // The "100%" mark for the gauge
    private Double quarterlyTarget;

    private String month; // e.g., "JANUARY"
    private Integer year; // e.g., 2024
}