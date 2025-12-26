package com.braininventory.leadsphere.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class SocialLinks {
    private String linkedin;
    private String twitter;
    private String website;
}