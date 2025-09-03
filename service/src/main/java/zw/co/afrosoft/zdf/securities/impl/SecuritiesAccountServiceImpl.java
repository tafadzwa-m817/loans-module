package zw.co.afrosoft.zdf.securities.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.entity.SecuritiesAccount;
import zw.co.afrosoft.zdf.securities.SecuritiesAccountRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesAccountService;



@Slf4j
@Service
@RequiredArgsConstructor
public class SecuritiesAccountServiceImpl implements SecuritiesAccountService {

    private final SecuritiesAccountRepository securitiesAccountRepository;

    /**
     * Retrieves a paginated list of securities accounts filtered by force number and membership number,
     * ordered descending by ID.
     *
     * @param forceNumber      optional filter by force number
     * @param membershipNumber optional filter by membership number
     * @param pageable        pagination information
     * @return paginated securities accounts
     */
    @Override
    public Page<SecuritiesAccount> getSecuritiesAccounts(String forceNumber, String membershipNumber, Pageable pageable) {
        log.debug("Fetching securities accounts for forceNumber: {}, membershipNumber: {}", forceNumber, membershipNumber);
        return securitiesAccountRepository.findAllByOrderByIdDesc(forceNumber, membershipNumber, pageable);
    }
}

