package zw.co.afrosoft.zdf.loans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LoanAccountService {
    Page<LoanAccount> getLoanAccounts(String forceNumber, String membershipNumber,Pageable pageable);
}
