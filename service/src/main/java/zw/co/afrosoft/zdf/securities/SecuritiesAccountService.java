package zw.co.afrosoft.zdf.securities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.afrosoft.zdf.entity.SecuritiesAccount;



/**
 * Service interface for managing securities accounts.
 */
public interface SecuritiesAccountService {

    /**
     * Retrieves a paginated list of securities accounts filtered by force number and membership number.
     *
     * @param forceNumber       optional filter by force number
     * @param membershipNumber  optional filter by membership number
     * @param pageable          pagination and sorting information
     * @return a page of matching securities accounts
     */
    Page<SecuritiesAccount> getSecuritiesAccounts(String forceNumber,
                                                  String membershipNumber,
                                                  Pageable pageable);
}

