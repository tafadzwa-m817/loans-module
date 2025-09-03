package zw.co.afrosoft.zdf.securities.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.entity.SecuritiesAccount;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.member.ServiceType;
import zw.co.afrosoft.zdf.securities.SecuritiesAccountRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static zw.co.afrosoft.zdf.enums.SecuritiesStatus.*;
import static zw.co.afrosoft.zdf.utils.enums.InterestCategory.SECURITIES;



@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecuritiesService {

    private final LoanRepository loanRepository;
    private final SecuritiesAccountRepository securitiesAccountRepository;
    private final MemberRepository memberRepository;
    private final SecuritiesRepository securitiesRepository;
    private final ParameterServiceClient parameterServiceClient;

    private static final String ACCOUNT_NUMBER_SUFFIX = "SEC";

    @Override
    public Double calculateLoanSecurity(Double loanAmount, int duration) {
        return calculateSecurityAmount(loanAmount, duration);
    }

    @Override
    public Double calculateProjectSecurity(Double projectAmount, int duration) {
        return calculateSecurityAmount(projectAmount, duration);
    }

    private Double calculateSecurityAmount(Double amount, int duration) {
        var interest = parameterServiceClient.getAll().getContent().stream()
                .filter(foundInterest -> foundInterest.interestCategory() == SECURITIES && foundInterest.isActive())
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("No active transaction type with category SECURITIES found"));
        return interest.interestPercentage() * amount * duration;
    }

    @Override
    public void addLoanSecurityTransaction(Long loanId, Double securityTransactionAmount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RecordNotFoundException(format("Loan with id %d not found", loanId)));

        Securities securities = new Securities();
        securities.setSecuritiesStatus(PENDING);
        securities.setBalance(securityTransactionAmount);
        securities.setLoanNumber(loan.getLoanNumber());
        securities.setForceNumber(loan.getForceNumber());
        securities.setSecuritiesAmount(securityTransactionAmount);
        securities.setTransactionDate(LocalDateTime.now());
        securities.setCurrencyId(loan.getCurrencyId());
        securities.setIsPaid(false);
        securities.setAudit(new Audit());
        securities.setSecuritiesAmountPaid(0.0d);

        Securities savedSecurities = securitiesRepository.save(securities);
        log.info("Saved loan securities: {}", savedSecurities);

        memberRepository.findMemberByForceNumber(loan.getForceNumber()).ifPresent(member -> {
            if (!securitiesAccountRepository.existsByForceNumberAndCurrencyId(member.getForceNumber(), loan.getCurrencyId())) {
                generateSecuritiesAccount(loan.getCurrencyId(), member.getServiceType());
            }
        });
    }

    private void generateSecuritiesAccount(Long currencyId, ServiceType serviceType) {
        List<SecuritiesAccount> newAccounts = loanRepository.findByLoanStatus(LoanStatus.OPEN).stream()
                .filter(loan -> Objects.equals(loan.getCurrencyId(), currencyId))
                .filter(loan -> !securitiesAccountRepository.existsByForceNumberAndCurrencyId(loan.getForceNumber(), currencyId))
                .map(loan -> {
                    log.info("Generating securities account for member with forceNumber {}", loan.getForceNumber());

                    String accountNumber = generateSecuritiesAccountNumber(serviceType);

                    return SecuritiesAccount.builder()
                            .securitiesAccountNumber(accountNumber)
                            .forceNumber(loan.getForceNumber())
                            .loans(List.of(loan))
                            .currencyId(currencyId)
                            .dateCreated(LocalDateTime.now())
                            .audit(new Audit())
                            .build();
                })
                .toList();

        if (!newAccounts.isEmpty()) {
            securitiesAccountRepository.saveAll(newAccounts);
        }
    }

    private String generateSecuritiesAccountNumber(ServiceType serviceType) {
        String prefix = switch (serviceType) {
            case ZIMBABWE_NATIONAL_ARMY -> "ZNA";
            case AIR_FORCE_OF_ZIMBABWE -> "AFZ";
        };

        AtomicInteger nextNumber = new AtomicInteger(1);

        securitiesAccountRepository.findTopBySecuritiesAccountNumberStartingWithOrderByIdDesc(prefix)
                .ifPresent(account -> {
                    try {
                        String numberPart = account.getSecuritiesAccountNumber().substring(prefix.length(), prefix.length() + 6);
                        nextNumber.set(Integer.parseInt(numberPart) + 1);
                    } catch (Exception e) {
                        log.warn("Failed to parse securities account number '{}'", account.getSecuritiesAccountNumber(), e);
                    }
                });

        return prefix + String.format("%06d", nextNumber.get()) + ACCOUNT_NUMBER_SUFFIX;
    }

    @Override
    public Securities paySecurity(Long id, SecuritiesStatus securitiesStatus, Double transactionAmount) {
        Securities transaction = securitiesRepository.findByIdAndSecuritiesStatus(id, securitiesStatus)
                .orElseThrow(() -> new RecordNotFoundException(
                        format("Security transaction with id %d and status %s not found", id, securitiesStatus)));

        transaction.setSecuritiesAmountPaid(transactionAmount);

        if (transaction.getSecuritiesAmount() <= transactionAmount) {
            transaction.setIsPaid(true);
            transaction.setSecuritiesStatus(PAID);
        } else {
            transaction.setIsPaid(false);
            transaction.setSecuritiesStatus(OVERDUE);
        }

        return securitiesRepository.save(transaction);
    }

    @Override
    public Page<Securities> getAllSecurityTransactions(String loanNumber, String forceNumber, Long currencyId,
                                                       SecuritiesStatus securitiesStatus, Pageable pageable) {
        return securitiesRepository.findAllByOrderByIdDesc(loanNumber, forceNumber, currencyId, securitiesStatus, pageable);
    }
}
