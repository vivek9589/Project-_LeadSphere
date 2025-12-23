package com.braininventory.leadsphere.lead_service.entity;


import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "leads")
@Data
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String opportunityName;
    private Double value;

    @Enumerated(EnumType.STRING)
    private LeadStatus status; // e.g., NEW, WON, LOST

    private String source;
    private String owner; // Map this to 'assignedTo' logic

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}