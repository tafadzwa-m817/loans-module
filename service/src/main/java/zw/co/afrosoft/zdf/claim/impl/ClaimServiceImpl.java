package zw.co.afrosoft.zdf.claim.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zw.co.afrosoft.zdf.claim.*;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.entity.Securities;
import zw.co.afrosoft.zdf.enums.LoanStatus;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.enums.SecuritiesStatus;
import zw.co.afrosoft.zdf.exceptions.ClaimRejectedException;
import zw.co.afrosoft.zdf.exceptions.RecordNotFoundException;
import zw.co.afrosoft.zdf.feign.Currency;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanAccountRepository;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.member.Member;
import zw.co.afrosoft.zdf.member.MemberRepository;
import zw.co.afrosoft.zdf.securities.SecuritiesRepository;
import zw.co.afrosoft.zdf.subscription.AccountStatus;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccount;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static zw.co.afrosoft.zdf.claim.ClaimType.RETIREMENT_CLAIM;
import static zw.co.afrosoft.zdf.enums.LoanStatus.OPEN;
import static zw.co.afrosoft.zdf.enums.LoanType.LOAN;
import static zw.co.afrosoft.zdf.enums.LoanType.PROJECT;
import static zw.co.afrosoft.zdf.member.MemberStatus.*;
import static zw.co.afrosoft.zdf.subscription.CurrencyType.USD;

/**
 * created by  Romeo Jerenyama
 * created on  5/5/2025 at 18:23
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final LoanRepository loanRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final SubscriptionsAccountRepository subscriptionsAccountRepository;
    private final SecuritiesRepository securitiesRepository;
    private final ClaimRepository claimRepository;
    private final ParameterServiceClient parameterServiceClient;
    private final MemberRepository memberRepository;

    @Override
    public Claim processClaim(ClaimantDetailsRequest request, String forceNumber, ClaimType claimType, LocalDate retirementDate) {
        log.info("Processing claim for forceNumber: {}", forceNumber);

        // Step 1: Validate input and retrieve core entities
        validateClaimInputs(forceNumber, claimType, request);
        Optional<Member> memberOpt = memberRepository.findByForceNumberAndMemberStatus(forceNumber, ACTIVE);
        Currency localCurrency = getLocalCurrency();

        // Step 2: Retrieve accounts and loans
        List<SubscriptionsAccount> subscriptionsAccounts = getSubscriptions(forceNumber);
        double loanUsdBalance = 0.0;
        double loanLocalBalance = 0.0;
        double projectUsdBalance = 0.0;
        double projectLocalBalance = 0.0;

        Loan loan = getLoan(forceNumber, LOAN);
        Loan project = getLoan(forceNumber, PROJECT);

        if (loan != null) {
            if (Objects.equals(loan.getCurrencyId(), localCurrency.getId())) {
                loanLocalBalance += loan.getBalance();
            } else {
                loanUsdBalance += loan.getBalance();
            }
        }

        if (project != null) {
            if (Objects.equals(project.getCurrencyId(), localCurrency.getId())) {
                projectLocalBalance += project.getBalance();
            } else {
                projectUsdBalance += project.getBalance();
            }
        }

        // Step 3: Calculate balances
        double subscriptionTotalBalance = consolidateSubscriptionBalance(forceNumber);
        double loanAndProjectTotalBalance = consolidateLoanAndProjectBalance(forceNumber);
        double loanProjectSecuritiesBalance = consolidateLoanProjectSecurities(forceNumber);

        double claimAmount = subscriptionTotalBalance - loanAndProjectTotalBalance;
        if (claimAmount < 0) {
            throw new ClaimRejectedException("Claim rejected due to insufficient subscription balance.");
        }

        // Step 4: Breakdown securities and account balances by currency
        Map<String, Double> subscriptionBalances = calculateSubscriptionBalances(subscriptionsAccounts, forceNumber, localCurrency);

        // Securities breakdown per loan type
        double loanSecuritiesLocal = calculateLoanProjectLocalSecuritiesBalance(loan);
        double loanSecuritiesUsd = calculateLoanProjectUsdSecuritiesBalance(loan);
        double projectSecuritiesLocal = calculateLoanProjectLocalSecuritiesBalance(project);
        double projectSecuritiesUsd = calculateLoanProjectUsdSecuritiesBalance(project);

        // Step 5: Build and persist claim
        Claim claim = buildClaim(
                request, forceNumber, claimType, retirementDate,
                claimAmount, subscriptionBalances,
                loanAndProjectTotalBalance, loanProjectSecuritiesBalance,
                subscriptionTotalBalance, loanAndProjectTotalBalance
        );
        claim.setLoanUsdBalance(loanUsdBalance);
        claim.setLoanLocalBalance(loanLocalBalance);
        claim.setProjectUsdBalance(projectUsdBalance);
        claim.setProjectLocalBalance(projectLocalBalance);
        claim.setCurrencyId(localCurrency.getId());
        claim.setLoanSecuritiesLocalBalance(loanSecuritiesLocal);
        claim.setLoanSecuritiesUsdBalance(loanSecuritiesUsd);
        claim.setProjectSecuritiesLocalBalance(projectSecuritiesLocal);
        claim.setProjectSecuritiesUsdBalance(projectSecuritiesUsd);

        Claim savedClaim = claimRepository.save(claim);
        log.info("Saved claim: {}", savedClaim);

        // Step 6: Finalize accounts and update member status
        finalizeAccounts(forceNumber);
        updateMemberStatus(memberOpt, claimType);

        return savedClaim;
    }


    @Override
    public Claim getMemberClaim(Long id) {
        requireNonNull(id, "Id cannot be null");
        log.info("Getting member claim: {}", id);
        return claimRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(
                        format("Claim with id %s not found", id)
                ));
    }

    private void validateClaimInputs(String forceNumber, ClaimType claimType, ClaimantDetailsRequest request) {
        requireNonNull(forceNumber, "Force number is required");
        requireNonNull(claimType, "Claim type is required");
        requireNonNull(request, "Claimant details request is required");
    }

    private List<Securities> getSecurities(String forceNumber) {
        return securitiesRepository.findAllByForceNumberAndIsPaid(forceNumber, false);
    }

    private List<SubscriptionsAccount> getSubscriptions(String forceNumber) {
        return subscriptionsAccountRepository.findAll().stream()
                .filter(acc -> forceNumber.equals(acc.getForceNumber()))
                .toList();
    }

    private Currency getLocalCurrency() {
        return parameterServiceClient.getAllCurrency().stream()
                .filter(currency -> !USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Local currency not found"));
    }

    private Loan getLoan(String forceNumber, LoanType type) {
        return loanRepository.findByForceNumberAndLoanStatusAndLoanType(forceNumber, OPEN, type)
                .orElse(null);
    }

    private Claim buildClaim(
            ClaimantDetailsRequest request, String forceNumber, ClaimType claimType, LocalDate retirementDate,
            double claimAmount,
            Map<String, Double> subscriptionBalances,
            double loanAndProjectTotal, double loanProjectSecuritiesBalance, double subscriptionTotalBalance,
            double totalOwing
    ) {
        var member = memberRepository.findMemberByForceNumber(forceNumber);
        Claim claim = new Claim();
        member.ifPresent(foundMember -> {
            claim.setAccountHolderName(foundMember.getPersonalDetails().getFirstName());
            claim.setAccountHolderSurname(foundMember.getPersonalDetails().getLastName());
            switch (claimType){
                case DEATH_CLAIM -> foundMember.setMemberStatus(INACTIVE_DEATH);
                case RETIREMENT_CLAIM -> foundMember.setMemberStatus(INACTIVE_RETIRED);
            }
            memberRepository.save(foundMember);
        });
        claim.setClaimStatus(ClaimStatus.APPROVED);
        claim.setClaimAmount(roundToTwoDecimalPlaces(claimAmount));
        claim.setClaimType(claimType);
        claim.setApprovedDate(LocalDate.now());
        claim.setForceNumber(forceNumber);
        claim.setRetirementDate(claimType == RETIREMENT_CLAIM ? retirementDate : null);
        claim.setRate(getLocalCurrency().getRate().doubleValue());
        claim.setClaimantDetails(ClaimantDetails.builder()
                .claimantName(request.getClaimantName())
                .claimantSurname(request.getClaimantSurname())
                .claimantIdNumber(request.getClaimantIdNumber())
                .claimantAddress(request.getClaimantAddress())
                .claimantPhone(request.getClaimantPhone())
                .claimantEmail(request.getClaimantEmail())
                .build());
        claim.setSubscriptionLocalCurrencyBalance(roundToTwoDecimalPlaces(subscriptionBalances.getOrDefault("local", 0.0)));
        claim.setSubscriptionUSDBalance(roundToTwoDecimalPlaces(subscriptionBalances.getOrDefault("usd", 0.0)));
        claim.setLoanProjectSecuritiesBalance(loanProjectSecuritiesBalance);
        claim.setTotalLoanProjectBalance(roundToTwoDecimalPlaces(loanAndProjectTotal));
        claim.setSubscriptionTotalBalance(roundToTwoDecimalPlaces(subscriptionTotalBalance));
        claim.setTotalOwing(roundToTwoDecimalPlaces(totalOwing));
        claim.setAudit(new Audit());

        return claim;
    }

    private void finalizeAccounts(String forceNumber) {
        zeroingAndClosingLoanAndProject(forceNumber);
        closeSubscriptions(forceNumber);
        closeLoanAccount(forceNumber);
        closeSecurities(forceNumber);
    }

    private void updateMemberStatus(Optional<Member> memberOpt, ClaimType claimType) {
        memberOpt.ifPresent(member -> {
            switch (claimType) {
                case RETIREMENT_CLAIM -> member.setMemberStatus(INACTIVE_RETIRED);
                case DEATH_CLAIM -> member.setMemberStatus(INACTIVE_DEATH);
            }
            memberRepository.save(member);
        });
    }

    private Map<String, Double> calculateSecuritiesBalance(List<Securities> securities, Loan loan) {
        if (loan == null || securities == null) {
            return Map.of("local", 0.0, "usd", 0.0);
        }

        Long localCurrencyId = getLocalCurrency().getId();
        double local = 0.0, usd = 0.0;

        for (Securities sec : securities) {
            if (loan.getLoanNumber().equals(sec.getLoanNumber())) {
                if (Objects.equals(sec.getCurrencyId(), localCurrencyId)) {
                    local += sec.getBalance();
                } else {
                    usd += sec.getBalance();
                }
            }
        }

        return Map.of(
                "local", roundToTwoDecimalPlaces(local),
                "usd", roundToTwoDecimalPlaces(usd)
        );
    }
    private double calculateLoanProjectLocalSecuritiesBalance(Loan loan) {
        if (loan != null && loan.getLoanNumber() != null) {
            return  securitiesRepository.findAll().stream()
                    .filter(sec -> Objects.equals(sec.getCurrencyId(), getLocalCurrency().getId()))
                    .filter(securities -> Objects.equals(securities.getForceNumber(), loan.getForceNumber()))
                    .mapToDouble(Securities::getBalance)
                    .sum();
        }
        return 0.0;
    }
    private double calculateLoanProjectUsdSecuritiesBalance(Loan loan) {
        if (loan != null && loan.getLoanNumber() != null) {
            return  securitiesRepository.findAll().stream()
                    .filter(sec -> !Objects.equals(sec.getCurrencyId(), getLocalCurrency().getId()))
                    .filter(securities -> Objects.equals(securities.getForceNumber(), loan.getForceNumber()))
                    .mapToDouble(Securities::getBalance)
                    .sum();
        }
        return 0.0;
    }
    private Map<String, Double> calculateLoanBalances(
            List<Loan> loans, String forceNumber, Currency localCurrency) {

        if (loans.isEmpty() || forceNumber == null || localCurrency == null) {
            return Map.of("local", 0.0, "usd", 0.0);
        }

        double local = 0.0;
        double usd = 0.0;
        Long localCurrencyId = localCurrency.getId();

        for (Loan loan : loans) {
            if (!forceNumber.equals(loan.getForceNumber())) continue;
            double balance = loan.getBalance() != null ? loan.getBalance() : 0.0;
            if (Objects.equals(loan.getCurrencyId(), localCurrencyId)) {
                local += balance;
            } else {
                usd += balance;
            }
        }

        return Map.of(
                "local", roundToTwoDecimalPlaces(local),
                "usd", roundToTwoDecimalPlaces(usd)
        );
    }

    private Map<String, Double> calculateSubscriptionBalances(
            List<SubscriptionsAccount> accounts, String forceNumber, Currency localCurrency) {

        if (accounts == null || forceNumber == null || localCurrency == null) {
            return Map.of("local", 0.0, "usd", 0.0);
        }

        double local = 0.0;
        double usd = 0.0;
        Long localCurrencyId = localCurrency.getId();

        for (SubscriptionsAccount acc : accounts) {
            if (!forceNumber.equals(acc.getForceNumber())) continue;

            double balance = acc.getCurrentBalance() != null ? acc.getCurrentBalance().doubleValue() : 0.0;
            if (Objects.equals(acc.getCurrencyId(), localCurrencyId)) {
                local += balance;
            } else {
                usd += balance;
            }
        }

        return Map.of(
                "local", roundToTwoDecimalPlaces(local),
                "usd", roundToTwoDecimalPlaces(usd)
        );
    }

    private double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    @Override
    public Page<Claim> retrieveClaims(String forceNumber, ClaimStatus claimStatus, ClaimType claimType, Pageable pageable) {
        return claimRepository.findAllByOrderByIdDesc(forceNumber, claimType, claimStatus, pageable);
    }

    @Override
    public List<ClaimResponse> getClaimCountsGroupedByStatus() {
        log.info("Fetching number of APPROVED or CLAIMED claims in the past 60 days grouped by status");

        LocalDate cutoffDate = LocalDate.now().minusDays(60);
        List<ClaimRepository.ClaimStatusCount> results = claimRepository.countRecentApprovedOrClaimedClaims(cutoffDate);

        List<ClaimResponse> response = results.stream()
                .map(claimStatusCount -> new ClaimResponse(
                        claimStatusCount.getCount().intValue(),
                        claimStatusCount.getStatus()))
                .collect(Collectors.toList());

        log.info("Retrieved {} grouped claim entries for the past 60 days", response.size());
        return response;
    }


    private void closeLoanAccount(String forceNumber) {
        log.info("Closing loan account: {}", forceNumber);
        loanAccountRepository.findAllByForceNumber(forceNumber).forEach(loanAccount -> {
            loanAccount.setAccountStatus(AccountStatus.CLOSED);
            loanAccountRepository.save(loanAccount);
        });
    }

    private void zeroingAndClosingLoanAndProject(String forceNumber) {
        requireNonNull(forceNumber, "Force number cannot be null");
        log.info("Attempting to close loans and projects for forceNumber: {}", forceNumber);

        closeLoanOrProject(forceNumber, LOAN);
        closeLoanOrProject(forceNumber, PROJECT);
    }

    private void closeLoanOrProject(String forceNumber, LoanType loanType) {
        var optionalLoan = loanRepository.findByForceNumberAndLoanStatusAndLoanType(forceNumber, OPEN, loanType);
        if (optionalLoan.isPresent()) {
            var loan = optionalLoan.get();
            log.info("Closing {}: {}", loanType, loan.getLoanNumber());
            loan.setBalance(0.0);
            loan.setLoanStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);
        } else {
            log.warn("No open {} found for forceNumber: {}", loanType, forceNumber);
        }
    }

    private double consolidateLoanProjectSecurities(String forceNumber){
        List<Currency> currencies = parameterServiceClient.getAllCurrency();
        requireNonNull(forceNumber, "Force number cannot be null");
        log.info("Consolidating loan and project securities: {}", currencies);

        Currency localCurrency = currencies.stream()
                .filter(currency -> !USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Local currency not found"));

        Currency usdCurrency = currencies.stream()
                .filter(currency -> USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("USD currency not found"));

        List<Securities> unpaidLoanAndProjectSecurities = securitiesRepository.findAll().stream()
                .filter(securities -> !securities.getIsPaid())
                .toList();
        if (unpaidLoanAndProjectSecurities.isEmpty()) {
            return 0.0;
        }
        double ussdBalance = unpaidLoanAndProjectSecurities.stream()
                .filter(securities -> Objects.equals(securities.getCurrencyId(), usdCurrency.getId()) &&
                        Objects.equals(forceNumber, securities.getForceNumber()))
                .filter(securities -> !securities.getIsPaid())
                .mapToDouble(Securities::getBalance)
                .findFirst()
                .orElse(0.0);

        log.info("Securities Usd balance: {}", ussdBalance);
        double localBalance = unpaidLoanAndProjectSecurities.stream()
                .filter(securities -> Objects.equals(securities.getCurrencyId(), localCurrency.getId()) &&
                        Objects.equals(forceNumber, securities.getForceNumber()))
                .filter(securities -> !securities.getIsPaid())
                .mapToDouble(Securities::getBalance)
                .findFirst()
                .orElse(0.0);
        log.info("Securities Local balance: {}", localBalance);
        return (ussdBalance * localCurrency.getRate().doubleValue()) + localBalance;
    }
    private double consolidateLoanAndProjectBalance(String forceNumber){
        List<Currency> currencies = parameterServiceClient.getAllCurrency();

        log.info("Consolidating loan and project balance: {}", forceNumber);

        Currency usdCurrency = currencies.stream()
                .filter(currency -> USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("USD currency not found"));

        Currency localCurrency = currencies.stream()
                .filter(currency -> !USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Local currency not found"));

        List<Loan> openLoanAndProject = loanRepository.findAll().stream()
                .filter(loan -> Objects.equals(loan.getLoanStatus(), OPEN) &&
                        Objects.equals(forceNumber, loan.getForceNumber()))
                .toList();

        double localBalance = openLoanAndProject.stream()
                .filter(loan -> Objects.equals(loan.getCurrencyId(), localCurrency.getId()))
                .mapToDouble(Loan::getBalance)
                .findFirst()
                .orElse(0.0);

        double usdBalance = openLoanAndProject.stream()
                .filter(loan -> Objects.equals(loan.getCurrencyId(), usdCurrency.getId()))
                .mapToDouble(Loan::getBalance)
                .findFirst()
                .orElse(0.0);

        return (usdBalance * localCurrency.getRate().doubleValue()) + localBalance;
    }
    private double consolidateSubscriptionBalance(String forceNumber) {
        requireNonNull(forceNumber, "Force number cannot be null");
        List<Currency> currencies = parameterServiceClient.getAllCurrency();

        Currency usdCurrency = currencies.stream()
                .filter(currency -> USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("USD currency not found"));

        Currency localCurrency = currencies.stream()
                .filter(currency -> !USD.name().equals(currency.getCurrencyName()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Local currency not found"));

        List<SubscriptionsAccount> accounts = subscriptionsAccountRepository.findAll().stream()
                .filter(account -> Objects.equals(account.getForceNumber(), forceNumber))
                .toList();

        double usdBalance = accounts.stream()
                .filter(account -> Objects.equals(account.getCurrencyId(), usdCurrency.getId()))
                .mapToDouble(account -> account.getCurrentBalance().doubleValue())
                .findFirst()
                .orElse(0.0);

        double localBalance = accounts.stream()
                .filter(account -> Objects.equals(account.getCurrencyId(), localCurrency.getId()))
                .mapToDouble(account -> account.getCurrentBalance().doubleValue())
                .findFirst()
                .orElse(0.0);

        return (usdBalance * localCurrency.getRate().doubleValue()) + localBalance;
    }
    private void closeSubscriptions(String forceNumber) {
        requireNonNull(forceNumber, "Force number cannot be null");

        List<SubscriptionsAccount> accounts = subscriptionsAccountRepository.findAllByForceNumber(forceNumber);
        accounts.forEach(acc -> {
            log.info("Closing subscription account: {}", acc);
            acc.setCurrentBalance(BigDecimal.ZERO);
            acc.setAccountStatus(AccountStatus.CLOSED);
            acc.setCurrentArrears(BigDecimal.ZERO);
            acc.setBalanceBForward(BigDecimal.ZERO);
            subscriptionsAccountRepository.save(acc);
        });
    }

    private void closeSecurities(String forceNumber) {
        requireNonNull(forceNumber, "Force number cannot be null");

        List<Securities> securitiesList = securitiesRepository.findAllByForceNumber(forceNumber);
        securitiesList.forEach(securities -> {
            log.info("Closing securities: {}", securities);
            securities.setSecuritiesStatus(SecuritiesStatus.CLOSED);
            securities.setBalance(0.0);
            securities.setIsPaid(true);
            securities.setSecuritiesAmount(0.0);
            securities.setSecuritiesAmountPaid(0.0);
            securities.setOverdraftAmount(0.0);
            securitiesRepository.save(securities);
        });
    }


}
