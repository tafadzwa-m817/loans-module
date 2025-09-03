package zw.co.afrosoft.zdf.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubscriptionService {

    /**
     * Adds a currency to an existing subscription account.
     *
     * @param accountId  the ID of the subscription account
     * @param currencyId the ID of the currency to add
     * @return the updated subscription account
     */
    SubscriptionsAccount addCurrency(Long accountId, Long currencyId);

    /**
     * Updates member details by performing a bulk upload from a file.
     *
     * @param file         the Excel/CSV file containing member data
     * @param forceNumbers optional list of force numbers to limit the update scope
     * @return list of updated subscription accounts
     */
    List<SubscriptionsAccount> updateMemberDetailsFromFile(MultipartFile file, List<String> forceNumbers);

    /**
     * Retrieves a paginated list of subscriptions filtered by force number, account number, and currency ID.
     *
     * @param forceNumber   optional filter by force number
     * @param accountNumber optional filter by account number
     * @param currencyId    optional filter by currency
     * @param pageable      pagination details
     * @return a page of matching subscription accounts
     */
    Page<SubscriptionsAccount> getAllSubscriptions(String forceNumber,
                                                   String accountNumber,
                                                   Long currencyId,
                                                   Pageable pageable);

    /**
     * Retrieves subscription account details by ID.
     *
     * @param id the subscription account ID
     * @return subscription account DTO
     */
    SubscriptionsAccountDto getSubscriptionById(Long id);

    /**
     * Applies a given interest rate to all subscriptions.
     *
     * @param interest the interest rate as a percentage (e.g., 5.5 for 5.5%)
     * @return list of updated subscription accounts
     */
    List<SubscriptionsAccount> applyInterestToSubscriptions(float interest);
}
