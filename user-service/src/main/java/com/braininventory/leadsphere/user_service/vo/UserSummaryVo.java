package com.braininventory.leadsphere.user_service.vo;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor // This is required for the 'new' keyword in JPQL
public class UserSummaryVo {
    private Long ownerId;    // Maps to u.id (Long)
    private String ownerName; // Maps to CONCAT(...) (String)
}