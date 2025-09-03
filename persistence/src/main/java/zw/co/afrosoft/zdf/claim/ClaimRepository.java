package zw.co.afrosoft.zdf.claim;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



public interface ClaimRepository extends JpaRepository<Claim, Long> {
    @Query("SELECT c FROM Claim c " +
            "WHERE (:forceNumber IS NULL OR c.forceNumber = :forceNumber) " +
            "AND (:claimType IS NULL OR c.claimType = :claimType) " +
            "AND (:claimStatus IS NULL OR c.claimStatus = :claimStatus)")
    Page<Claim> findAllByOrderByIdDesc(@Param("forceNumber") String forceNumber,
                                       @Param("claimType") ClaimType claimType,
                                       @Param("claimStatus") ClaimStatus claimStatus,
                                       Pageable pageable);

    Optional<Claim> findByForceNumberAndClaimStatus(String forceNumber, ClaimStatus claimStatus);

    @Query("""
        SELECT c.claimStatus AS status, COUNT(c) AS count
        FROM Claim c
        WHERE
            (c.claimStatus = 'APPROVED' AND c.approvedDate >= :cutoffDate)
            OR
            (c.claimStatus = 'CLAIMED' AND c.claimDate >= :cutoffDate)
        GROUP BY c.claimStatus
    """)
    List<ClaimStatusCount> countRecentApprovedOrClaimedClaims(@Param("cutoffDate") LocalDate cutoffDate);

    interface ClaimStatusCount {
        ClaimStatus getStatus();
        Long getCount();
    }
}
