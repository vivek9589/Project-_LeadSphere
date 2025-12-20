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
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String phoneNo;
    private String source;

    private LeadStatus status;
    private String assignedTo;

    private Date createdAt;

}
