package zw.co.afrosoft.zdf.securities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.afrosoft.zdf.entity.SecuritiesAccount;
import zw.co.afrosoft.zdf.enums.LoanType;

import java.util.Optional;


public interface SecuritiesAccountRepository extends JpaRepository<SecuritiesAccount, Long> {
    Optional<SecuritiesAccount> findTopByCurrencyIdOrderByIdDesc(Long currencyId);
    boolean existsByForceNumberAndCurrencyId(String forceNumber, Long currencyId);

    @Query("SELECT l FROM LoanAccount l " +
            "WHERE (:forceNumber IS NULL OR l.forceNumber = :forceNumber)" +
            "AND (:membershipNumber IS NULL OR l.membershipNumber = :membershipNumber)" +
            "ORDER BY l.id DESC")
    Page<SecuritiesAccount> findAllByOrderByIdDesc(@Param("forceNumber")String forceNumber,
                                                   @Param("membershipNumber")String membershipNumber,
                                                   Pageable pageable);

    Optional<SecuritiesAccount> findTopBySecuritiesAccountNumberStartingWithOrderByIdDesc(String prefix);

    //boolean existsByForceNumber(String forceNumber);
}
