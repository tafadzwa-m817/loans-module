package zw.co.afrosoft.zdf.interest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.afrosoft.zdf.entity.Audit;
import zw.co.afrosoft.zdf.enums.LoanType;
import zw.co.afrosoft.zdf.enums.PaymentType;
import zw.co.afrosoft.zdf.exceptions.*;
import zw.co.afrosoft.zdf.feign.TransactionCategory;
import zw.co.afrosoft.zdf.feign.TransactionType;
import zw.co.afrosoft.zdf.feign.clients.GLServiceClient;
import zw.co.afrosoft.zdf.feign.clients.ParameterServiceClient;
import zw.co.afrosoft.zdf.feign.dto.ErrorResponse;
import zw.co.afrosoft.zdf.feign.dto.Interest;
import zw.co.afrosoft.zdf.loans.Loan;
import zw.co.afrosoft.zdf.loans.LoanRepository;
import zw.co.afrosoft.zdf.subscription.AccountStatus;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccount;
import zw.co.afrosoft.zdf.subscription.SubscriptionsAccountRepository;
import zw.co.afrosoft.zdf.subscription.dto.ProcessTransTypeRequest;
import zw.co.afrosoft.zdf.transaction.TransactionRepository;
import zw.co.afrosoft.zdf.transaction.Transactions;
import zw.co.afrosoft.zdf.utils.enums.InterestCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.LocalDate.now;
import static zw.co.afrosoft.zdf.enums.LoanStatus.OPEN;
import static zw.co.afrosoft.zdf.enums.LoanType.LOAN;
import static zw.co.afrosoft.zdf.enums.PaymentType.LOAN_INTEREST;
import static zw.co.afrosoft.zdf.enums.PaymentType.PROJECT_INTEREST;
import static zw.co.afrosoft.zdf.utils.enums.InterestCategory.*;



@Slf4j
@Service
@RequiredArgsConstructor
public class InterestApplicationServiceImpl implements InterestApplicationService {
    private final LoanRepository loanRepository;
    private final SubscriptionsAccountRepository subscriptionsAccountRepository;
    private final ParameterServiceClient parameterServiceClient;
    private final GLServiceClient glServiceClient;
    private final InterestTrackingRepository interestTrackingRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public InterestResponse applyInterest() {
        log.info("Starting interest application process");
        YearMonth nextExecutionMonth = getNextExecutionMonth();
        log.info("Determined next execution month: {}", nextExecutionMonth);

        YearMonth currentMonth = YearMonth.now();
        log.debug("Current month is: {}", currentMonth);

        if (nextExecutionMonth.isAfter(currentMonth) || nextExecutionMonth.equals(currentMonth)) {
            log.warn("Attempted to apply interest for invalid month. Next execution: {}, Current: {}", nextExecutionMonth, currentMonth);
            throw new InvalidExecutionDateException("Cannot apply interest for future/current month: " + nextExecutionMonth + ". Current month is: " + currentMonth);
        }
        log.info("Validated execution month. Proceeding with interest application for {}", nextExecutionMonth);

        List<Loan> loans = new ArrayList<>();
        List<Loan> projects = new ArrayList<>();
        List<SubscriptionsAccount> subscriptions = new ArrayList<>();

        log.info("Setting up interest handlers for different transaction categories");
        Map<TransactionCategory, Consumer<TransactionType>> interestHandlers = Map.of(
                TransactionCategory.LOANS_INTEREST, transactionType -> {
                    log.info("Processing loan interest for transaction type ID: {}", transactionType.getId());
                    loans.addAll(applyLoanInterest(nextExecutionMonth, transactionType.getId()));
                },
                TransactionCategory.PROJECTS_INTEREST, transactionType -> {
                    log.info("Processing project interest for transaction type ID: {}", transactionType.getId());
                    projects.addAll(applyProjectInterest(nextExecutionMonth, transactionType.getId()));
                },
                TransactionCategory.SUBSCRIPTIONS_INTEREST, transactionType -> {
                    log.info("Processing subscription interest for transaction type ID: {}", transactionType.getId());
                    subscriptions.addAll(applySubscriptionInterest(nextExecutionMonth, transactionType.getId()));
                }
        );

        List<TransactionType> transactionTypes = fetchTransactionTypes();

        transactionTypes.stream()
                .filter(transactionType -> {
                    boolean isValid = interestHandlers.containsKey(transactionType.getTransactionCategory());
                    if (!isValid) {
                        log.debug("Skipping transaction type {} - not an interest category", transactionType.getCode());
                    }
                    return isValid;
                })
                .forEach(transactionType -> {
                    log.info("Processing interest for transaction type: {} ({})",
                            transactionType.getCode(), transactionType.getTransactionCategory());
                    interestHandlers.get(transactionType.getTransactionCategory()).accept(transactionType);
                });

        log.info("Building interest application response");
        log.info("Interest application summary - Loans: {}, Projects: {}, Subscriptions: {}",
                loans.size(), projects.size(), subscriptions.size());

        String executedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Interest application executed by: {}", executedBy);

        InterestResponse interestResponse = InterestResponse.builder()
                .message(format("Interest for %s applied successfully", nextExecutionMonth))
                .numberOfLoansAffected(loans.size())
                .numberOfProjectsAffected(projects.size())
                .numberOfSubscriptionsAffected(subscriptions.size())
                .interestRunBy(executedBy)
                .interestRunAt(LocalDateTime.now())
                .build();

        updateLastRunMonth(nextExecutionMonth);

        log.info("Interest application process completed successfully");
        return interestResponse;
    }

    @Override
    public YearMonth getNextExecutionMonth() {
        log.info("Determining next execution month");
        return interestTrackingRepository.findLastExecutionDate()
                .map(lastDate -> lastDate.getYearMonth().plusMonths(1))
                .orElse(YearMonth.now().minusMonths(1));
    }

    private void updateLastRunMonth(YearMonth executedMonth) {
        log.info("Updating last run month to: {}", executedMonth);
        interestTrackingRepository.save(InterestTracking.builder()
                .lastExecutionDate(executedMonth.atEndOfMonth()).build());
    }

    /**
     * Apply interest to loans
     */
    private List<Loan> applyLoanInterest(YearMonth month, Long transId) {
        log.info("Starting loan interest application for month: {} and transaction ID: {}", month, transId);

        var lastTracking = interestTrackingRepository.findTopByOrderByLastExecutionDateDesc();

        if (lastTracking.isPresent() && lastTracking.get().getYearMonth().equals(month)) {
            log.info("Interest already applied for loans in {}", month);
            throw new InterestAlreadyAppliedException("Interest already applied for loans in " + month);
        }

        var interest = fetchInterest(InterestCategory.LOAN);

        if (interest == null || interest.interestPercentage() == 0) {
            log.warn("No valid interest rate found for loan interest application");
            throw new InvalidInterestCategoryException(
                    String.format("No valid interest rate found for loan interest application: %s", interest)
            );
        }

        double interestRate = interest.interestPercentage();
        log.info("Applying interest rate: {}%", interestRate);


        List<Loan> loansToUpdate = loanRepository.findAllByLoanStatusAndLoanType(OPEN, LOAN)
                .stream()
                .filter(loan -> loan.getBalance() != null && loan.getBalance() > 0)
                .peek(loan -> {
                    double balance = Optional.of(loan.getBalance()).orElse(0.0);
                    double totalInterestAmount = Optional.ofNullable(loan.getTotalInterestAmount()).orElse(0.0);
                    double interestToDate = Optional.ofNullable(loan.getInterestToDate()).orElse(0.0);

                    double loanInterest = interestRate * balance;

                    loan.setBalance(balance + loanInterest);
                    loan.setInterestToDate(interestToDate + loanInterest);
                    loan.setInterestAmount(loanInterest);
                    loan.setTotalInterestAmount(totalInterestAmount + loanInterest);

                    log.info("Applied interest of {} to loan with ID: {}. New balance: {}", loanInterest, loan.getId(), loan.getBalance());
                })
                .toList();
        var savedLoans = loanRepository.saveAll(loansToUpdate);
        updateLoanWithInterest(transId, savedLoans, interest);
        return savedLoans;
    }


    private void updateLoanWithInterest(Long transId, List<Loan> loansToUpdate, Interest interest) {
        log.info("Starting to update loans with interest for transaction ID: {}", transId);
        if (!loansToUpdate.isEmpty()) {
            List<Transactions> updatedTransactions = new ArrayList<>();
            log.info("Processing {} loans for interest update", loansToUpdate.size());
            loanRepository.saveAll(loansToUpdate).forEach(loan -> {
                var transType = parameterServiceClient.getById(transId);
                assert transType != null;
                log.info("Creating transaction for loan with force number: {}", loan.getForceNumber());
                var savedTransaction = transactionRepository.save(Transactions.builder()
                        .forceNumber(loan.getForceNumber())
                        .transactionCode(transType.getCode())
                        .transactionDate(now())
                        .datePosted(LocalDateTime.now())
                        .audit(new Audit())
                        .amount(BigDecimal.valueOf(interest.interestPercentage() * loan.getBalance()))
                        .referenceId(loan.getId())
                        .currencyId(loan.getCurrencyId())
                        .paymentType(Objects.equals(loan.getLoanType(), LOAN) ? LOAN_INTEREST : PROJECT_INTEREST)
                        .build());
                updatedTransactions.add(savedTransaction);
            });
            log.info("Successfully updated loans with interest details for transaction ID: {}", transId);
            log.info("Posting transactions ---------------------------------> {}",updatedTransactions);
            postTransactions(updatedTransactions);
        }
    }

    /**
     * Apply interest to Projects
     */
    private List<Loan> applyProjectInterest(YearMonth month, Long transId) {
        log.info("Starting project interest application for month: {} and transaction ID: {}", month, transId);

        var lastTracking = interestTrackingRepository.findTopByOrderByLastExecutionDateDesc();

        if (lastTracking.isPresent() && lastTracking.get().getYearMonth().equals(month)) {
            log.info("Interest already applied for projects in {}", month);
            throw new InterestAlreadyAppliedException("Interest already applied for projects in " + month);
        }

        var interest = fetchInterest(PROJECT);

        if (interest == null || interest.interestPercentage() == 0) {
            log.warn("No valid interest rate found for project interest application");
            throw new InvalidInterestCategoryException(
                    String.format("No valid interest rate found for project interest application: %s", interest)
            );
        }

        double interestRate = interest.interestPercentage();
        log.info("Applying interest for project, rate: {}%", interestRate);


        List<Loan> loansToUpdate = loanRepository.findAllByLoanStatusAndLoanType(OPEN, LoanType.PROJECT)
                .stream()
                .filter(loan -> loan.getBalance() != null && loan.getBalance() > 0)
                .peek(loan -> {
                    double balance = Optional.of(loan.getBalance()).orElse(0.0);
                    double interestToDate = Optional.ofNullable(loan.getInterestToDate()).orElse(0.0);
                    double totalInterestAmount = Optional.ofNullable(loan.getTotalInterestAmount()).orElse(0.0);

                    double loanInterest = interestRate * balance;

                    loan.setBalance(balance + loanInterest);
                    loan.setInterestToDate(interestToDate + loanInterest);
                    loan.setInterestAmount(loanInterest);
                    loan.setTotalInterestAmount(totalInterestAmount + loanInterest);

                    log.info("Applied interest of {} to project with ID: {}. New balance: {}", loanInterest, loan.getId(), loan.getBalance());
                })
                .toList();

        var savedLoans = loanRepository.saveAll(loansToUpdate);
        updateLoanWithInterest(transId, savedLoans, interest);
        log.info("Successfully updated projects with interest details for transaction ID: {}", transId);
        return savedLoans;
    }

    /**
     * Apply interest to subscriptions
     */
    private List<SubscriptionsAccount> applySubscriptionInterest(YearMonth month, Long transId) {
        var lastTracking = interestTrackingRepository.findTopByOrderByLastExecutionDateDesc();
        boolean alreadyApplied = lastTracking.isPresent() && lastTracking.get().getYearMonth().equals(month);

        if (alreadyApplied) {
            log.info("Subscription interest already applied for {}", month);
            throw new InterestAlreadyAppliedException("Interest already applied for subscription in " + month);
        }

        var interest = fetchInterest(SUBSCRIPTION);

        var transType = parameterServiceClient.getById(transId);
        if (transType == null) {
            throw new RecordNotFoundException("Transaction type not found for ID: " + transId);
        }

        List<SubscriptionsAccount> updatedAccounts = new ArrayList<>();
        List<Transactions> transactionsToPost = new ArrayList<>();

        subscriptionsAccountRepository.findAllByAccountStatus(AccountStatus.ACTIVE).forEach(account -> {
            if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) <= 0) return;

            // Store previous balance
            account.setBalanceBForward(account.getCurrentBalance());

            // Calculate and apply interest
            BigDecimal subInterest = account.getCurrentBalance()
                    .multiply(BigDecimal.valueOf(interest.interestPercentage()));

            account.setCurrentBalance(account.getCurrentBalance().add(subInterest));
            account.setInterestToDate(
                    (account.getInterestToDate() == null ? 0 : account.getInterestToDate()) + interest.interestPercentage()
            );

            updatedAccounts.add(account);

            updatedAccounts.forEach(subscriptionsAccount -> {
                BigDecimal percentageRate = BigDecimal.valueOf(interest.interestPercentage());
                transactionsToPost.add(Transactions.builder()
                        .forceNumber(account.getForceNumber())
                        .transactionCode(transType.getCode())
                        .transactionDate(LocalDate.now())
                        .datePosted(LocalDateTime.now())
                        .audit(new Audit())
                        .amount(subscriptionsAccount.getCurrentBalance().multiply(percentageRate))
                        .referenceId(account.getId())
                        .currencyId(account.getCurrencyId())
                        .paymentType(PaymentType.SUBSCRIPTION_INTEREST)
                        .build());
            });
        });

        // Save updated accounts and transactions
        List<SubscriptionsAccount> savedAccounts = new ArrayList<>(subscriptionsAccountRepository.saveAll(updatedAccounts));
        transactionRepository.saveAll(transactionsToPost);

        log.info("Applied subscription interest to {} accounts", updatedAccounts.size());
        log.info("Posting subs transactions ---------------------------------> {}",transactionsToPost);
        postTransactions(transactionsToPost);
        return savedAccounts;
    }

    private void postTransactions(List<Transactions> transactions) {
        log.info("-------------------Starting transaction posting process-------------------------");
        try {
            log.info("Posting transaction to General Ledger");
            List<ProcessTransTypeRequest> requests = new ArrayList<>();
            transactions.forEach(foundTransactions -> {

                requests.add(new ProcessTransTypeRequest(
                        foundTransactions.getTransactionCode(),
                        foundTransactions.getAmount(),
                        foundTransactions.getForceNumber(),
                        format("%s payment for %s", foundTransactions.getPaymentType(),
                                foundTransactions.getForceNumber()
                        )));
            });

            log.info("Requests built to be posted------------------------------------------->{}",requests);

            log.info("Posting {} transaction entries to GL", requests.size());
            glServiceClient.postTransactionEntries(requests);
            log.info("Successfully posted transactions to GL");

        } catch (FeignException.BadRequest e) {
            log.info("error {}", e.contentUTF8());
            String errorResponse = e.contentUTF8();
            ErrorResponse response = objectMapper(errorResponse);
            throw new PostTransactionException(response.error());
        } catch (FeignException e) {
            log.error("Error retrieving account details via feign: {}", e.getMessage());
            throw new RuntimeException("Error posting transactions via Feign: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches all transaction types from the parameter service
     *
     * @return List of transaction types
     * @throws InvalidTransactionCategoryException if there's an error fetching transaction types
     */
    private List<TransactionType> fetchTransactionTypes() {
        log.info("Fetching all transaction types from parameter service");
        try {
            List<TransactionType> transactionTypes = parameterServiceClient.getAllTransactionTypes().getContent();
            log.info("Retrieved {} transaction types", transactionTypes.size());
            return transactionTypes;
        } catch (FeignException.BadRequest e) {
            log.error("Bad request while fetching transaction types: {}", e.getMessage());
            throw new InvalidTransactionCategoryException("Failed to fetch transaction types - Bad Request: " + e.getMessage());
        } catch (FeignException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching transaction types: {} - {}", e.status(), errorMessage);
            throw new InvalidTransactionCategoryException(errorMessage);
        } catch (Exception e) {
            log.error("Unexpected error while fetching transaction types: {}", e.getMessage(), e);
            throw new InvalidTransactionCategoryException(e.getMessage());
        }
    }

    /**
     * Fetches an interest in category from the parameter service
     *
     * @param category the interest category to search for
     * @return Interest matching the given category
     * @throws InvalidInterestCategoryException if there's an error fetching interests
     * @throws RecordNotFoundException          if no matching interest is found
     */
    private Interest fetchInterest(InterestCategory category) {
        log.info("Fetching interest with category: {}", category);
        try {
            List<Interest> interests = parameterServiceClient.getAll().getContent();
            log.info("Retrieved {} interests", interests.size());

            return interests.stream()
                    .filter(retrievedInterest -> retrievedInterest.interestCategory() == category)
                    .findFirst()
                    .orElseThrow(() -> new RecordNotFoundException("No interest found with category: " + category));
        } catch (Exception e) {
            log.error("Unexpected error while fetching interests: {}", e.getMessage(), e);
            throw new InvalidInterestCategoryException(e.getMessage());
        }
    }


    private ErrorResponse objectMapper(String errorResponse) {
        log.info("Parsing error response from service");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse response;
        try {
            response = objectMapper.readValue(errorResponse, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}



