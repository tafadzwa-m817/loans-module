package zw.co.afrosoft.zdf.securities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;



/**
 * Service interface for managing securities related to loans and projects.
 */
public interface SecuritiesService {

    /**
     * Calculates the loan security amount based on the loan amount and duration.
     *
     * @param loanAmount the principal loan amount
     * @param duration   the duration of the loan (e.g., in months)
     * @return the calculated loan security amount
     */
    Double calculateLoanSecurity(Double loanAmount, int duration);

    /**
     * Calculates the project security amount based on the project amount and duration.
     *
     * @param projectAmount the total project amount
     * @param duration      the duration of the project (e.g., in months)
     * @return the calculated project security amount
     */
    Double calculateProjectSecurity(Double projectAmount, int duration);

    /**
     * Adds a loan security transaction for the specified loan.
     *
     * @param loanId the ID of the loan
     * @param amount the amount to be added as security
     */
    void addLoanSecurityTransaction(Long loanId, Double amount);

    /**
     * Processes a payment towards a security.
     *
     * @param id                the ID of the security transaction
     * @param transactionStatus the status of the transaction (e.g., PAID, PENDING)
     * @param transactionAmount the amount to pay towards the security
     * @return the updated Securities entity after payment
     */
    Securities paySecurity(Long id, SecuritiesStatus transactionStatus, Double transactionAmount);

    /**
     * Retrieves a paginated list of all security transactions filtered by given parameters.
     *
     * @param loanNumber       optional filter by loan number
     * @param forceNumber      optional filter by member force number
     * @param currencyId       optional filter by currency ID
     * @param securitiesStatus optional filter by status of the security
     * @param pageable         pagination and sorting information
     * @return a page of securities matching the filter criteria
     */
    Page<Securities> getAllSecurityTransactions(String loanNumber,
                                                String forceNumber,
                                                Long currencyId,
                                                SecuritiesStatus securitiesStatus,
                                                Pageable pageable);
}
