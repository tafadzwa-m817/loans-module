package zw.co.afrosoft.zdf.securities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.enums.SecuritiesCategory;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;

import java.util.List;
import java.util.Optional;


public interface SecuritiesRepository extends JpaRepository<Securities, Long> {
    Optional<Securities> findBySecuritiesCategoryAndId(SecuritiesCategory category, Long id);
    Page<Securities> findAllByLoanNumberAndSecuritiesStatus(String loanNumber, SecuritiesStatus status,
                                                            Pageable pageable);
    Page<Securities> findAllByLoanNumberOrderByIdDesc(String loanNumber, Pageable pageable);
    Optional<Securities> findByIdAndSecuritiesStatus(Long id, SecuritiesStatus status);
    Optional<Securities> findByForceNumber(String forceNumber);
    Optional<Securities> findByForceNumberAndCurrencyId(String forceNumber, Long currencyId);
    Optional<Securities> findByForceNumberAndCurrencyIdAndLoanNumber(String forceNumber, Long currencyId, String loanNumber);
    List<Securities> findAllByForceNumber(String forceNumber);
    List<Securities> findAllByForceNumberAndBalanceGreaterThan(String forceNumber, double balance);

    @Query("SELECT s FROM Securities s " +
            "WHERE (:loanNumber IS NULL OR s.loanNumber = :loanNumber)" +
            "AND (:forceNumber IS NULL OR s.forceNumber = :forceNumber)" +
            "AND (:currencyId IS NULL OR s.currencyId = :currencyId)" +
            "AND (:securitiesStatus IS NULL OR s.securitiesStatus = :securitiesStatus)")
    Page<Securities> findAllByOrderByIdDesc(@Param("loanNumber") String loanNumber,
                                            @Param("forceNumber") String forceNumber,
                                            @Param("currencyId") Long currencyId,
                                            @Param("securitiesStatus") SecuritiesStatus securitiesStatus,
                                            Pageable pageable);

    boolean existsByForceNumberAndCurrencyId(String forceNumber, Long currencyId);

    List<Securities> findAllByForceNumberAndIsPaid(String forceNumber, boolean isPaid);
    List<Securities> findAllBySecuritiesCategory(SecuritiesCategory category);
}
