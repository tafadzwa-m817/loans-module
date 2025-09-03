package zw.co.afrosoft.zdf.loans.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.loans.LoanAccount;
import zw.co.afrosoft.zdf.loans.LoanAccountRepository;
import zw.co.afrosoft.zdf.loans.LoanAccountService;



@Slf4j
@Service
@RequiredArgsConstructor
public class LoanAccountServiceImpl implements LoanAccountService {

    private final LoanAccountRepository loanAccountRepository;

    @Override
    public Page<LoanAccount> getLoanAccounts(String forceNumber, String membershipNumber, Pageable pageable) {
        return loanAccountRepository.findAllByOrderByIdDesc(forceNumber, membershipNumber, pageable);
    }
}
