package zw.co.afrosoft.zdf.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.afrosoft.zdf.enums.PaymentType;

import java.util.List;




/**
 * Service interface for managing transactions.
 */
public interface TransactionService {

    /**
     * Retrieves a paginated list of transactions filtered by various criteria.
     *
     * @param forceNumber  optional filter by member's force number
     * @param paymentTypes list of payment types to filter by (optional)
     * @param currencyId   optional filter by currency ID
     * @param referenceId  optional filter by reference ID (e.g., loan or project ID)
     * @param pageable     pagination and sorting information
     * @return a page of Transactions matching the filters
     */
    Page<Transactions> transactions(String forceNumber,
                                    List<PaymentType> paymentTypes,
                                    Long currencyId,
                                    Long referenceId,
                                    Pageable pageable);
}

