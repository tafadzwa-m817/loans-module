package zw.co.afrosoft.zdf.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.afrosoft.zdf.enums.PaymentType;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transactions,Long> ,
        JpaSpecificationExecutor<Transactions> {
    @Query("SELECT t FROM Transactions t WHERE t.referenceId = :referenceId AND" +
            " t.paymentType =:paymentType ORDER BY t.transactionDate DESC")
    List<Transactions> findRecentPaymentsByReferenceIdAndPaymentType(@Param("referenceId") Long referenceId,
                                                                    @Param("paymentType") PaymentType paymentType);

    List<Transactions> findAllByPaymentType(PaymentType paymentType);
}
