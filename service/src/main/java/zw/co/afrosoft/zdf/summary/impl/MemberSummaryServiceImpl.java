package zw.co.afrosoft.zdf.summary.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesRepository;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountRepository;
import zw.co.afrosoft.zdf.summary.MemberSummaryResponse;
import zw.co.afrosoft.zdf.summary.MemberSummaryService;

import java.util.Objects;

import static java.lang.String.join;
import static zw.co.afrosoft.zdf.enums.LoanStatus.OPEN;
import static zw.co.afrosoft.zdf.subscription.AccountStatus.ACTIVE;
import static zw.co.afrosoft.zdf.subscription.CurrencyType.USD;



@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSummaryServiceImpl implements MemberSummaryService {

    private final MemberRepository memberRepository;
    private final SubscriptionsAccountRepository subscriptionsAccountRepository;
    private final LoanRepository loanRepository;
    private final SecuritiesRepository securitiesRepository;
    private final ParameterServiceClient parameterServiceClient;

    @Override
    public MemberSummaryResponse getMemberSummary(String forceNumber) {
        log.info("Getting MemberSummary for forceNumber: {}", forceNumber);

        var localCurrency = parameterServiceClient.getAllCurrency().stream()
                .filter(currency -> !USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Local currency not found"));

        var member = memberRepository.findMemberByForceNumber(forceNumber)
                .orElseThrow(() -> new RecordNotFoundException("Member not found for force number: " + forceNumber));

        var loanList = loanRepository.findAllByForceNumberAndLoanStatus(forceNumber, OPEN);
        var subs = subscriptionsAccountRepository.findAllByForceNumberAndAccountStatus(forceNumber, ACTIVE);
        var securities = securitiesRepository.findAllByForceNumberAndBalanceGreaterThan(forceNumber, 0);

        MemberSummaryResponse response = MemberSummaryResponse.builder()
                .fullName(join(" ", member.getPersonalDetails().getFirstName(),
                        member.getPersonalDetails().getLastName()))
                .idNumber(member.getPersonalDetails().getNationalId())
                .serviceType(member.getServiceType())
                .build();

        // Accumulate loan balances
        for (var loan : loanList) {
            boolean isLocal = Objects.equals(loan.getCurrencyId(), localCurrency.getId());
            if (Objects.equals(loan.getLoanType(), LoanType.LOAN)) {
                if (isLocal) {
                    response.setLocalLoanBalance(safeAdd(response.getLocalLoanBalance(), loan.getBalance()));
                } else {
                    response.setUsdLoanBalance(safeAdd(response.getUsdLoanBalance(), loan.getBalance()));
                }
            } else {
                if (isLocal) {
                    response.setLocalProjectBalance(safeAdd(response.getLocalProjectBalance(), loan.getBalance()));
                } else {
                    response.setUsdProjectBalance(safeAdd(response.getUsdProjectBalance(), loan.getBalance()));
                }
            }
        }

        // Accumulate subscription balances
        for (var sub : subs) {
            boolean isLocal = Objects.equals(sub.getCurrencyId(), localCurrency.getId());
            double balance = sub.getCurrentBalance().doubleValue();
            if (isLocal) {
                response.setLocalSubsBalance(safeAdd(response.getLocalSubsBalance(), balance));
            } else {
                response.setUsdSubsBalance(safeAdd(response.getUsdSubsBalance(), balance));
            }
        }

        // Accumulate security balances
        for (var sec : securities) {
            boolean isLocal = Objects.equals(sec.getCurrencyId(), localCurrency.getId());
            if (isLocal) {
                response.setLocalSecBalance(safeAdd(response.getLocalSecBalance(), sec.getBalance()));
            } else {
                response.setUsdSecBalance(safeAdd(response.getUsdSecBalance(), sec.getBalance()));
            }
        }

        return response;
    }

    // Helper to safely add nullable Doubles
    private Double safeAdd(Double a, Double b) {
        return (a == null ? 0.0 : a) + (b == null ? 0.0 : b);
    }

}
