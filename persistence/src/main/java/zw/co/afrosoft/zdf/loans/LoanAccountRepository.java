package zw.co.afrosoft.zdf.loans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
    Optional<LoanAccount> findTopByAccountTypeOrderByIdDesc(AccountType accountType);

    boolean existsByForceNumber(String forceNumber);

    boolean existsByForceNumberAndAccountType(String forceNumber, AccountType accountType);

    @Query("SELECT l FROM LoanAccount l " +
            "WHERE (:forceNumber IS NULL OR l.forceNumber = :forceNumber)" +
            "AND (:membershipNumber IS NULL OR l.membershipNumber = :membershipNumber)" +
            "ORDER BY l.id DESC")
    Page<LoanAccount> findAllByOrderByIdDesc(@Param("forceNumber")String forceNumber,
                                             @Param("membershipNumber")String membershipNumber,
                                             Pageable pageable);

    List<LoanAccount> findAllByForceNumber(String forceNumber);
}
