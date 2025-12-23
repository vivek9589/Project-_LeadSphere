package com.braininventory.leadsphere.lead_service.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class LeadDto {
    private Long id;
    private String owner;
    private String source;
    private Boolean converted;
    private LocalDate createdDate;

}
