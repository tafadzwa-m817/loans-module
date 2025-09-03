package zw.co.afrosoft.zdf.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SubscriptionsAccountRepository extends JpaRepository<SubscriptionsAccount, Long>,
        JpaSpecificationExecutor<SubscriptionsAccount> {
    Optional<SubscriptionsAccount> findTopByOrderByIdDesc();
    @Query("SELECT s FROM SubscriptionsAccount s " +
            "WHERE (:forceNumber IS NULL OR s.forceNumber = :forceNumber) " +
            "AND (:accountNumber IS NULL OR s.accountNumber = :accountNumber) " +
            "AND (:currencyId IS NULL OR s.currencyId = :currencyId)")
    Page<SubscriptionsAccount> findAllByOrderByIdDesc(@Param("forceNumber") String forceNumber,
                                               @Param("accountNumber") String accountNumber,
                                               @Param("currencyId") Long currencyId,
                                               Pageable pageable);

    Optional<SubscriptionsAccount> findByForceNumber(String forceNumber);
    Optional<SubscriptionsAccount> findByForceNumberAndCurrencyId(String forceNumber, Long currencyId);
    List<SubscriptionsAccount> getByForceNumber(String forceNumber);
    SubscriptionsAccount findByForceNumberIgnoreCase(String forceNumber);
    List<SubscriptionsAccount> findAllByAccountStatus(AccountStatus accountStatus);
    boolean existsByCurrencyIdAndForceNumber(Long currencyId, String forceNumber);
    List<SubscriptionsAccount> findAllByForceNumber(String forceNumber);
    List<SubscriptionsAccount> findAllByForceNumberAndAccountStatus(String forceNumber, AccountStatus accountStatus);
}
