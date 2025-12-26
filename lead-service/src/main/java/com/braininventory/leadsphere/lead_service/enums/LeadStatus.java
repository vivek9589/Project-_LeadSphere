package com.braininventory.leadsphere.lead_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LeadStatus {
    NEW,
    QUALIFIED,
    PROPOSITION,
    WON;

    @JsonCreator
    public static LeadStatus fromString(String value) {
        return value == null ? null : LeadStatus.valueOf(value.toUpperCase());
    }
}
