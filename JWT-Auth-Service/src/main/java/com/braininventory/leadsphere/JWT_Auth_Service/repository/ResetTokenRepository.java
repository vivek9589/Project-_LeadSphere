package com.braininventory.leadsphere.JWT_Auth_Service.repository;

import com.braininventory.leadsphere.JWT_Auth_Service.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
    Optional<ResetToken> findByUserId(Long userId);
}
